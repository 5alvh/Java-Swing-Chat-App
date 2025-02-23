package cliente;

import java.awt.*;
import javax.swing.*;
import javax.swing.border.EmptyBorder;
import java.awt.event.KeyEvent;
import java.awt.event.KeyAdapter;

public class ClienteChat extends JFrame {
    private JPanel contentPane;
    private JTextArea textArea;
    private JTextField textField;
    private HiloCliente usuario;
    private String nickname;

    public static void main(String[] args) {
        EventQueue.invokeLater(() -> {
            String nickname = JOptionPane.showInputDialog("Ingrese su nombre de usuario:");
            if (nickname != null && !nickname.trim().isEmpty()) {
                try {
                    ClienteChat frame = new ClienteChat(nickname);
                    frame.setTitle("Chat - " + nickname);
                    frame.setVisible(true);
                } catch (Exception e) {
                    JOptionPane.showMessageDialog(null, 
                        "Error al conectar con el servidor. Asegúrese de que está iniciado.",
                        "Error de conexión",
                        JOptionPane.ERROR_MESSAGE);
                    System.exit(1);
                }
            } else {
                System.exit(0);
            }
        });
    }

    public ClienteChat(String nickname) {
        this.nickname = nickname;
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setBounds(100, 100, 500, 350);
        contentPane = new JPanel();
        contentPane.setBorder(new EmptyBorder(5, 5, 5, 5));
        setContentPane(contentPane);

        GridBagLayout gbl_contentPane = new GridBagLayout();
        gbl_contentPane.columnWidths = new int[]{400, 80, 0};
        gbl_contentPane.rowHeights = new int[]{250, 30};
        gbl_contentPane.columnWeights = new double[]{1.0, 0.0, 0.0};
        gbl_contentPane.rowWeights = new double[]{1.0, 0.0};
        contentPane.setLayout(gbl_contentPane);

        textArea = new JTextArea();
        textArea.setEditable(false);
        textArea.setLineWrap(true);
        textArea.setWrapStyleWord(true);

        JScrollPane scrollPane = new JScrollPane(textArea);
        scrollPane.setVerticalScrollBarPolicy(JScrollPane.VERTICAL_SCROLLBAR_AS_NEEDED);
        scrollPane.setHorizontalScrollBarPolicy(JScrollPane.HORIZONTAL_SCROLLBAR_NEVER);

        GridBagConstraints gbc_scrollPane = new GridBagConstraints();
        gbc_scrollPane.gridwidth = 2;
        gbc_scrollPane.insets = new Insets(5, 5, 5, 5);
        gbc_scrollPane.fill = GridBagConstraints.BOTH;
        gbc_scrollPane.gridx = 0;
        gbc_scrollPane.gridy = 0;
        contentPane.add(scrollPane, gbc_scrollPane);

        // Botón de ayuda
        JButton btnHelp = new JButton("?");
        btnHelp.addActionListener(e -> {
            JOptionPane.showMessageDialog(this,
                "Para enviar un mensaje privado use:\n" +
                "/p nombreUsuario mensaje\n\n" +
                "Ejemplo: /p Juan Hola Juan!",
                "Ayuda",
                JOptionPane.INFORMATION_MESSAGE);
        });

        GridBagConstraints gbc_btnHelp = new GridBagConstraints();
        gbc_btnHelp.anchor = GridBagConstraints.NORTH;
        gbc_btnHelp.insets = new Insets(5, 5, 5, 0);
        gbc_btnHelp.gridx = 2;
        gbc_btnHelp.gridy = 0;
        contentPane.add(btnHelp, gbc_btnHelp);

        textField = new JTextField();
        GridBagConstraints gbc_textField = new GridBagConstraints();
        gbc_textField.gridwidth = 2;
        gbc_textField.insets = new Insets(5, 5, 5, 5);
        gbc_textField.fill = GridBagConstraints.HORIZONTAL;
        gbc_textField.gridx = 0;
        gbc_textField.gridy = 1;
        contentPane.add(textField, gbc_textField);
        textField.setColumns(30);

        JButton btnEnviar = new JButton("Enviar");
        GridBagConstraints gbc_btnEnviar = new GridBagConstraints();
        gbc_btnEnviar.insets = new Insets(5, 5, 5, 0);
        gbc_btnEnviar.gridx = 2;
        gbc_btnEnviar.gridy = 1;
        contentPane.add(btnEnviar, gbc_btnEnviar);

        JButton btnSalir = new JButton("Salir");
        GridBagConstraints gbc_btnSalir = new GridBagConstraints();
        gbc_btnSalir.anchor = GridBagConstraints.NORTH;
        gbc_btnSalir.insets = new Insets(5, 5, 5, 0);
        gbc_btnSalir.gridx = 2;
        gbc_btnSalir.gridy = 0;
        contentPane.add(btnSalir, gbc_btnSalir);

        btnEnviar.addActionListener(e -> enviarMensaje());
        
        btnSalir.addActionListener(e -> {
            usuario.cerrarConexion();
            System.exit(0);
        });

        textField.addKeyListener(new KeyAdapter() {
            @Override
            public void keyPressed(KeyEvent e) {
                if (e.getKeyCode() == KeyEvent.VK_ENTER) {
                    enviarMensaje();
                }
            }
        });

        usuario = new HiloCliente(this, nickname);
        Thread hilo = new Thread(usuario);
        hilo.start();
    }

    private void enviarMensaje() {
        String mensaje = textField.getText().trim();
        if (!mensaje.isEmpty()) {
            usuario.enviarMensaje(mensaje);
            textField.setText("");
        }
    }

    public void mostrarMensaje(String mensaje) {
        textArea.append(mensaje);
        textArea.setCaretPosition(textArea.getDocument().getLength());
    }
}
