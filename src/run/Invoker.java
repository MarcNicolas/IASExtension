/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */

package run;

import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;

/**
 *
 * @author marc
 */
public class Invoker {

  public final static String SITOOLS_MAIN_CLASS = "fr.cnes.sitools.server.Starter";

  public static void main(String[] args) throws Exception {

        String[] args1 = new String[1];
        
        args1[0] = SITOOLS_MAIN_CLASS;
        args = args1;
        if (args.length != 1) {
            System.err.println("Sitools Main class is not set");
            System.exit(1);
        }

        Class[] argTypes = new Class[1];
        argTypes[0] = String[].class;
        try {
            Method mainMethod = Class.forName(args[0]).getDeclaredMethod("main", argTypes);
            Object[] argListForInvokedMain = new Object[1];
            argListForInvokedMain[0] = new String[0];
            mainMethod.invoke(null,argListForInvokedMain);
        } catch (ClassNotFoundException ex) {
            System.err.println("Class " + args[0] + "not found in classpath.");
        } catch (NoSuchMethodException ex) {
            System.err.println("Class " + args[0] + "does not define public static void main(String[])");
        } catch (InvocationTargetException ex) {
            System.err.println("Exception while executing " + args[0] + ":" + ex.getTargetException());
        } catch (IllegalAccessException ex) {
            System.err.println("main(String[]) in class " + args[0] + " is not public");
        }
  }
}
