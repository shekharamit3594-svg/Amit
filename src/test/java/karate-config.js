function fn() {
      let env = karate.env; // get system property 'karate.env'
  karate.log('karate.env system property was:', env);
  if (!env) {
    env = 'dev';
  }

    // Connection Timeout --> we will ensure that it waits for 5 seconds for the API Connectivity
    karate.configure('connectTimeout', 5000);
    //ReadTimeout --> will ensure that it waits for 30 seconds for the response to be generated
    karate.configure('readTimeout', 30000);

    //Retry configuration
  karate.configure('retry', {
    count: 3,
    interval: 10000
  });

  // ── UI Driver Toggle ──────────────────────────────────────────────────────────
  // Controls WHERE the browser runs — independent of karate.env (API environment).
  //
  //   Local  (default): mvn test
  //   Remote (Grid)   : mvn test -Dwebdriver.remote=true
  //   Custom Grid URL : mvn test -Dwebdriver.remote=true -Dgrid.url=http://host:4444
  //
  // karate.env still controls the API base URL / tokens (dev, e2e, etc.)
  // These two settings are orthogonal — any combination is valid.
  let isRemote = karate.properties['webdriver.remote'] === 'true';
  let gridUrl  = karate.properties['grid.url'] || 'http://selenium-hub:4444';

  // ── Auto-screenshot on failure ─────────────────────────────────────────────────
  // afterScenario runs after every scenario (API and UI).
  // If the scenario failed, karate.info.errorMessage is non-null.
  // screenshot() is wrapped in try/catch because it throws when no browser session
  // is active (e.g. pure API scenarios). The image is embedded in the HTML report.
  karate.configure('afterScenario', function() {
    if (karate.info.errorMessage) {
      try { karate.embed(karate.screenshot(), 'image/png') } catch(e) {}
    }
  });

  let driverConfig = isRemote
    ? { type: 'chrome', timeout: 20000, webDriverUrl: gridUrl + '/wd/hub' }
    : { type: 'chrome', timeout: 20000, addOptions: ['--no-sandbox', '--disable-dev-shm-usage'] };

  let config = {
    env: env,
    myVarName: 'someValue',
    driverConfig: driverConfig
  }

  if (env == 'dev') {
    // customize
    // e.g. config.foo = 'bar';
    config.bearerToken = 'Bearer 1470e5bb7fb3e1bc38d2dc4ceb91a43d8a7747ef0a808319560c1055a44fc37f'
    config.baseUrl = 'https://xpjhgxfthnjvkgftggik.supabase.co/functions/v1/rest-api'
    config.usersPath = '/public/v2/users'
    config.contentType = 'application/json'
  } else if (env == 'e2e') {
    // customize
  }
  return config;
}
