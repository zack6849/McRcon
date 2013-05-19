/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package com.zack6849.mcrcon;

/**
 *
 * @author Zack
 */
public class Test {
    public static void main(String[] args){
        System.out.println(Utils.encrypt("zack6849") + ":" + Utils.encrypt("testing"));
        System.out.println(Utils.encrypt("test") + ":" + Utils.encrypt("demo"));
    }
}
