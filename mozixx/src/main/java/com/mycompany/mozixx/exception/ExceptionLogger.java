package com.mycompany.mozixx.exception;

import com.mycompany.mozixx.config.JWT;

public class ExceptionLogger {

    private Class actualClass;

    public ExceptionLogger(Class actualClass) {
        this.actualClass = actualClass;
    }

    /**
     * Use this for JWT class
     * @param ex
     */
    public void errorLog(Exception ex) {
        StackTraceElement[] stackTrace = ex.getStackTrace();
        if (stackTrace.length > 0) {
            StackTraceElement element = stackTrace[0];
            for (StackTraceElement actualElement : stackTrace) {
                if (actualElement.getClassName().contains(this.actualClass.getName())) {
                    element = actualElement;
                    break;
                }
            }

            System.err.println(
                    "\n--------------- Exception ---------------"
                    + "\nStatus: JWT Class Exception"
                    + "\nStatus Code: 500"
                    + "\nException Message: " + ex.getMessage()
                    + "\nException Class: " + ex.getClass().getName()
                    + "\nFile: " + element.getFileName()
                    + "\nFunction: " + element.getMethodName() + "()"
                    + "\nLine: " + element.getLineNumber()
                    + "\nClass Loader Name: " + element.getClassLoaderName()
                    + "\nClass Name: " + element.getClassName()
                    + "\nModul Name: " + element.getModuleName()
                    + "\nModule Version: " + element.getModuleVersion()
                    + "\n--------------- Exception ---------------\n"
            );

        }
    }

}