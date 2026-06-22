function fn() {
      var env = karate.env; // get system property 'karate.env'
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
    interval: 1000
  });

  var config = {
    env: env,
    myVarName: 'someValue'
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