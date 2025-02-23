package server;

import java.io.IOException;
import java.net.ServerSocket;
import java.net.Socket;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class ServidorChat {
    private static Map<String, HiloServerChat> listaUsuarios = new HashMap<>();
    private static List<String> logMensajes = new ArrayList<>();


    public static void main(String[] args) {
        System.out.println("Iniciando Servidor.");
        
        Runtime.getRuntime().addShutdownHook(new Thread(() -> {
            System.out.println("\nServidor finalizando. Usuarios conectados:");
            listaUsuarios.keySet().forEach(System.out::println);
        }));
        ServerSocket server = null;

        try{
        	server = new ServerSocket(6000);
            while (true) {
                Socket usuario = server.accept();
                Thread hiloUsuario = new Thread(new HiloServerChat(usuario));
                hiloUsuario.start();
            }
        } catch (IOException e) {
            System.out.println("ERROR: " + e.getMessage());
        }finally{
        	
        }
    }

    public static synchronized boolean registarUsuario(HiloServerChat usuario, String nickname) {
        if (!listaUsuarios.containsKey(nickname)) {
            listaUsuarios.put(nickname, usuario);
            mostrarEstadoConexiones();
            return true;
        }
        return false;
    }

    public static synchronized void eliminarUsuario(String nickname) {
        listaUsuarios.remove(nickname);
        inviarAtodo("SEVER: ", nickname +"ha salido del chat.");
    }

    private static void mostrarEstadoConexiones() {
        System.out.println("NUMERO DE CONEXIONES ACTUALES: " + listaUsuarios.size());
    }

    public static void inviarAtodo(String nickname, String mensaje) {
        String mensajeFormateado = "[" + LocalDateTime.now().format(DateTimeFormatter.ofPattern("yy-MM-dd HH:mm")) + "] " + nickname + " -> " + mensaje + "\n";
        logMensajes.add(mensajeFormateado);
        
        for (HiloServerChat usuario : listaUsuarios.values()) {
            usuario.enviarMensaje(mensajeFormateado);
        }
    }

    public static void enviarPrivado(String emisor, String receptor, String mensaje) {
        HiloServerChat destinatario = listaUsuarios.get(receptor);
        if (destinatario != null) {
        	String mensajePrivado = "[" + LocalDateTime.now().format(DateTimeFormatter.ofPattern("yy-MM-dd HH:mm")) + "] [PRIVADO] " + emisor + " -> " + mensaje + "\n";
            destinatario.enviarMensaje(mensajePrivado);
            listaUsuarios.get(emisor).enviarMensaje(mensajePrivado);
        }
    }

    public static ArrayList<String> getLogsMensajes() {
        return new ArrayList<>(logMensajes);
    }
}