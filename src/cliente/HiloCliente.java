package cliente;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;
import java.net.Socket;

import javax.swing.JOptionPane;

public class HiloCliente implements Runnable {
	private Socket socket;
	private BufferedReader entrada;
	private BufferedWriter salida;
	private ClienteChat cliente;
	private String nickname;

	public HiloCliente(ClienteChat cliente, String nickname) {
		this.cliente = cliente;
		this.nickname = nickname;
		try {
			this.socket = new Socket("localhost", 6000);
			this.entrada = new BufferedReader(new InputStreamReader(socket.getInputStream()));
			this.salida = new BufferedWriter(new OutputStreamWriter(socket.getOutputStream()));
			enviarMensaje(nickname);
		} catch (IOException e) {
			JOptionPane.showMessageDialog(null, "IMPOSIBLE CONECTAR CON EL SERVIDOR \n" + e.getMessage(), "Error",
					JOptionPane.ERROR_MESSAGE);
			cerrarConexion();
			System.exit(0);
		}
	}

	public void enviarMensaje(String mensaje) {
		try {
			salida.write(mensaje + "\n");
			salida.flush();
		} catch (IOException e) {
			System.out.println("Error al enviar mensaje: " + e.getMessage());
			cerrarConexion();
		}
	}

	@Override
	public void run() {
		try {
			String mensaje;
			while (socket != null && !socket.isClosed() && (mensaje = entrada.readLine()) != null) {
				cliente.mostrarMensaje(mensaje + "\n");
			}
		} catch (IOException e) {
			if (!socket.isClosed()) {
				JOptionPane.showMessageDialog(null, "IMPOSIBLE CONECTAR CON EL SERVIDOR \n" + e.getMessage(), "Error",
						JOptionPane.ERROR_MESSAGE);
				System.exit(0);
			}
		} finally {
			cerrarConexion();
		}
	}

	public void cerrarConexion() {
		try {
			if (socket != null && !socket.isClosed()) {
				socket.close();
			}
			if (entrada != null) {
				entrada.close();
			}
			if (salida != null) {
				salida.close();
			}
		} catch (IOException e) {
			System.out.print("");
		}
	}

}
