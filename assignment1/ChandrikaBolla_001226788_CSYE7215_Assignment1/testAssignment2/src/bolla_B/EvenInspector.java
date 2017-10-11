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
public class EvenInspector extends Inspector{

    private String name;
    private LinkedList<Integer> list;
    
    public EvenInspector(String name){
        super(Inspector.InspectorType.Even.getValue());
        list=new LinkedList<Integer>();
        this.name=name;
        
        
        
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public LinkedList<Integer> getList() {
        return list;
    }

    public void setList(LinkedList<Integer> list) {
        this.list = list;
    }
    
    @Override
    public void inspect(int number) {
    //   System.out.println("This is "+this.name);
    
       if(number%2==0)
        {
            this.list.add(number);
        }
    
    }


    
}
