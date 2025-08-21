package Libreria.command;

import java.util.Stack;

public class Invoker {

    private Stack<Command> storico =  new Stack<>();

    public void esegui(Command c) {
        c.esegui();
        this.storico.push(c);
    }

    public void annulla_ultimo(){
        if (! this.storico.isEmpty()) {
            Command ultimo = this.storico.pop();
            ultimo.annulla();
        } else {
            System.out.println("Nessun comando da annullare");
        }
    }

}
