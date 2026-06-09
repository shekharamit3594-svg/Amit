package framework;

import lombok.experimental.StandardException;

@StandardException
public class GenericExceptions extends RuntimeException{

    //Whenever we say standard Exceptions, it generates four types of constructors automatically

    /*
        public GenericExceptions()
        public GenericExceptions(String msg)
        public GenericExceptions(String msg, Throwable t1)
        public RuntimeException(Throwable cause)
     */
}
