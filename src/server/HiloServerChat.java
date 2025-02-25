package server;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;
import java.net.Socket;

import javax.swing.JOptionPane;

public class HiloServerChat implements Runnable {
    private Socket socket;
    private BufferedReader entrada;
    private BufferedWriter salida;
    private String nickname;

    public HiloServerChat(Socket socket) {
        this.socket = socket;
        try {
            entrada = new BufferedReader(new InputStreamReader(socket.getInputStream()));
            salida = new BufferedWriter(new OutputStreamWriter(socket.getOutputStream()));
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public void enviarMensaje(String mensaje) {
        try {
            salida.write(mensaje);
            salida.flush();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    @Override
    public void run() {
        try {
            nickname = entrada.readLine();
            if (ServidorChat.registarUsuario(this, nickname)) {
            	
            	for (String mensaje : ServidorChat.getLogsMensajes()) {
                      salida.write(mensaje);
                      salida.flush();
                  }

                ServidorChat.inviarAtodo("SERVER", "Usuario " + nickname + " se ha conectado.");
                String mensaje;
                while ((mensaje = entrada.readLine()) != null) {
                    if (!mensaje.trim().isEmpty()) {
                        if (mensaje.startsWith("/p ")) {
                            String[] partes = mensaje.split(" ", 3);
                            if (partes.length >= 3) {
                                ServidorChat.enviarPrivado(nickname, partes[1], partes[2]);
                            }
                        } else {
                            ServidorChat.inviarAtodo(nickname, mensaje);
                        }
                    }
                }
            }
        } catch (IOException e) {
        	System.out.print("");
        } finally {
            ServidorChat.eliminarUsuario(nickname);
            
            try {
                socket.close();
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }
}