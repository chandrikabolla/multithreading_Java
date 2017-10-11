/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package bolla_B;

import java.util.LinkedList;

/**
 *
 * @author chand
 */
public abstract class Inspector {
    
    String name;
    public Inspector(String name){
        this.name=name;
    
    
}

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }
    
    public enum InspectorType{
            Even("Even"),
            Odd("Odd"),
            Order("Order"),
            Jack("Jack");
            
            private String value;

        public String getValue() {
            return value;
        }

        public void setValue(String value) {
            this.value = value;
        }
        private InspectorType(String value){
                this.value=value;
         }
            @Override
        public String toString(){
                   return value;
         }
    }
    public abstract void inspect(int number);
    
    
    
    
    
}
