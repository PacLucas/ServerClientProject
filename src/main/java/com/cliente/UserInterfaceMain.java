package com.cliente;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.node.ArrayNode;
import com.fasterxml.jackson.databind.node.ObjectNode;

import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

public class UserInterfaceMain {
    private JFrame frame;

    // Ponto

        // PANELS
    private JPanel mainPanel = new JPanel(new BorderLayout());;
    private JPanel buttonsPanel;
    private JPanel cadastroPontoPanel;
    private JPanel listagemPontoPanel;
    private JPanel edicaoPontoPanel;
    private JPanel exclusaoPontoPanel;

        // FIELDS
    private JTextField cadastroPontoNomeField;
    private JTextField cadastroPontoObsField;
    private JTextField idPontoField;

        // LABELS
    private JLabel cadastroPontoNomeLabel;
    private JLabel cadastroPontoObsLabel;
    private JLabel idPontoLabel;

        // BUTTONS
    private JButton cadastroPontoButton;
    private JButton listagemPontoButton;
    private JButton edicaoPontoButton;
    private JButton exclusaoPontoButton;



    // Segmento

        // PANELS
    private JPanel cadastroSegmentoPanel;
    private JPanel listagemSegmentoPanel;
    private JPanel edicaoSegmentoPanel;
    private JPanel exclusaoSegmentoPanel;

        // FIELDS
    private JTextField cadastroSegmentoPontoOrigemField;
    private JTextField cadastroSegmentoPontoDestinoField;
    private JTextField cadastroSegmentoDirecaoField;
    private JTextField cadastroSegmentoDistanciaField;
    private JTextField cadastroSegmentoObsField;
    private JTextField idSegmentoField;

        // LABELS
    private JLabel cadastroSegmentoPontoOrigemLabel;
    private JLabel cadastroSegmentoPontoDestinoLabel;
    private JLabel cadastroSegmentoDirecaoLabel;
    private JLabel cadastroSegmentoDistanciaLabel;
    private JLabel cadastroSegmentoObsLabel;
    private JLabel idSegmentoLabel;


        // BUTTONS
    private JButton cadastroSegmentoButton;
    private JButton listagemSegmentoButton;
    private JButton edicaoSegmentoButton;
    private JButton exclusaoSegmentoButton;

    // VARIABLES
    List<Pontos> listaPontos = new ArrayList<>();
    List<Segmentos> listaSegmentos = new ArrayList<>();
    JsonNode currentPonto = null;
    JPanel[] panels = {
        mainPanel,
        buttonsPanel,
        cadastroPontoPanel,
        listagemPontoPanel,
        edicaoPontoPanel,
        exclusaoPontoPanel,
        cadastroSegmentoPanel,
        listagemSegmentoPanel,
        edicaoSegmentoPanel,
        exclusaoSegmentoPanel
    };

    private Cliente cliente;

    public UserInterfaceMain(Cliente cliente) {
        this.cliente = cliente;
        frame = new JFrame("Gerenciamento de Rotas");
        frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        frame.setSize(1300, 400);

        // Panels
        cadastroPontoPanel = new JPanel(new GridLayout(5, 2, 10, 10));
        listagemPontoPanel = new JPanel(new GridLayout(1, 2, 10, 10));
        edicaoPontoPanel = new JPanel(new GridLayout(5, 2, 10, 10));
        exclusaoPontoPanel = new JPanel(new GridLayout(5, 2, 10, 10));

        cadastroSegmentoPanel = new JPanel(new GridLayout(5, 2, 10, 10));
        listagemSegmentoPanel = new JPanel(new GridLayout(1, 2, 10, 10));
        edicaoSegmentoPanel = new JPanel(new GridLayout(6, 2, 10, 10));
        exclusaoSegmentoPanel = new JPanel(new GridLayout(5, 2, 10, 10));

        // Fields
        cadastroPontoNomeLabel = new JLabel("Nome");
        cadastroPontoNomeField = new JTextField();
        cadastroPontoObsLabel = new JLabel("Observação");
        cadastroPontoObsField = new JTextField();
        idPontoLabel = new JLabel("ID");
        idPontoField = new JTextField();


        cadastroSegmentoPontoOrigemLabel = new JLabel("Ponto de Origem");
        cadastroSegmentoPontoOrigemField = new JTextField();
        cadastroSegmentoPontoDestinoLabel = new JLabel("Ponto de Destino");
        cadastroSegmentoPontoDestinoField = new JTextField();
        cadastroSegmentoDirecaoLabel = new JLabel("Direção");
        cadastroSegmentoDirecaoField = new JTextField();
        cadastroSegmentoDistanciaLabel = new JLabel("Distância");
        cadastroSegmentoDistanciaField = new JTextField();
        cadastroSegmentoObsLabel = new JLabel("Observação");
        cadastroSegmentoObsField = new JTextField();
        idSegmentoLabel = new JLabel("ID");
        idSegmentoField = new JTextField();

        // Buttons
        cadastroPontoButton = new JButton("Cadastrar Ponto");
        cadastroPontoButton.setFont(new Font("Arial", Font.BOLD, 14));
        listagemPontoButton = new JButton("Listar Pontos");
        listagemPontoButton.setFont(new Font("Arial", Font.BOLD, 14));
        edicaoPontoButton = new JButton("Editar Ponto");
        edicaoPontoButton.setFont(new Font("Arial", Font.BOLD, 14));
        exclusaoPontoButton = new JButton("Excluir Ponto");
        exclusaoPontoButton.setFont(new Font("Arial", Font.BOLD, 14));

        cadastroSegmentoButton = new JButton("Cadastrar Segmento");
        cadastroSegmentoButton.setFont(new Font("Arial", Font.BOLD, 14));
        listagemSegmentoButton = new JButton("Listar Segmentos");
        listagemSegmentoButton.setFont(new Font("Arial", Font.BOLD, 14));
        edicaoSegmentoButton = new JButton("Editar Segmento");
        edicaoSegmentoButton.setFont(new Font("Arial", Font.BOLD, 14));
        exclusaoSegmentoButton = new JButton("Excluir Segmento");
        exclusaoSegmentoButton.setFont(new Font("Arial", Font.BOLD, 14));

        // Panels
        cadastroPontoPanel.add(cadastroPontoNomeLabel);
        cadastroPontoPanel.add(cadastroPontoNomeField);
        cadastroPontoPanel.add(cadastroPontoObsLabel);
        cadastroPontoPanel.add(cadastroPontoObsField);
        for (int i = 2; i >= 0; i--) {
            cadastroPontoPanel.add(new JLabel(""));
            cadastroPontoPanel.add(new JLabel(""));
        }

        buttonsPanel = new JPanel(new FlowLayout());
        buttonsPanel.add(cadastroPontoButton);
        buttonsPanel.add(listagemPontoButton);
        buttonsPanel.add(edicaoPontoButton);
        buttonsPanel.add(exclusaoPontoButton);
        buttonsPanel.add(cadastroSegmentoButton);
        buttonsPanel.add(listagemSegmentoButton);
        buttonsPanel.add(edicaoSegmentoButton);
        buttonsPanel.add(exclusaoSegmentoButton);

        // Frame
        mainPanel.add(cadastroPontoPanel, BorderLayout.CENTER);
        mainPanel.add(buttonsPanel, BorderLayout.SOUTH);
        frame.getContentPane().add(mainPanel);

        // Listeners
        cadastroPontoButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                mudarTela("cadastro-ponto");

                cadastroPontoButton.removeActionListener(cadastroPontoActionListener);
                cadastroPontoButton.addActionListener(cadastroPontoActionListener);
            }
        });

        listagemPontoButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                String action = "listar-pontos";
                ObjectMapper mapper = new ObjectMapper();
                String token = cliente.getToken();

                ObjectNode requestData = mapper.createObjectNode();
                requestData.put("action", action);
                ObjectNode data = mapper.createObjectNode();
                data.put("token", token);
                requestData.set("data", data);

                try {
                    String jsonString = mapper.writeValueAsString(requestData);
                    cliente.sendRequestToServer(jsonString, action);
                    mudarTela("listar-pontos");
                } catch (JsonProcessingException ex) {
                    ex.printStackTrace();
                }
            }
        });

        edicaoPontoButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                mudarTela("edicao-ponto");

                edicaoPontoButton.removeActionListener(edicaoPontoActionListener);
                edicaoPontoButton.addActionListener(edicaoPontoActionListener);

            }
        });

        exclusaoPontoButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                mudarTela("exclusao-ponto");

                exclusaoPontoButton.removeActionListener(exclusaoPontoActionListener);
                exclusaoPontoButton.addActionListener(exclusaoPontoActionListener);
            }
        });


        cadastroSegmentoButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                mudarTela("cadastro-segmento");

                cadastroSegmentoButton.removeActionListener(cadastroSegmentoActionListener);
                cadastroSegmentoButton.addActionListener(cadastroSegmentoActionListener);
            }
        });

        listagemSegmentoButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                String action = "listar-segmentos";
                ObjectMapper mapper = new ObjectMapper();
                String token = cliente.getToken();

                ObjectNode requestData = mapper.createObjectNode();
                requestData.put("action", action);
                ObjectNode data = mapper.createObjectNode();
                data.put("token", token);
                requestData.set("data", data);

                try {
                    String jsonString = mapper.writeValueAsString(requestData);
                    listaSegmentos.clear();
                    cliente.sendRequestToServer(jsonString, action);
                    mudarTela("listar-segmentos");
                } catch (JsonProcessingException ex) {
                    ex.printStackTrace();
                }
            }
        });

        edicaoSegmentoButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                mudarTela("edicao-segmento");

                edicaoSegmentoButton.removeActionListener(edicaoSegmentoActionListener);
                edicaoSegmentoButton.addActionListener(edicaoSegmentoActionListener);

            }
        });

        exclusaoSegmentoButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                mudarTela("exclusao-segmento");

                exclusaoSegmentoButton.removeActionListener(exclusaoSegmentoActionListener);
                exclusaoSegmentoButton.addActionListener(exclusaoSegmentoActionListener);
            }
        });

    }

    private void limparTela() {
        for (JPanel elemento : panels) {
            if ((elemento != null) && (elemento.getComponentCount() > 0)) {
                elemento.removeAll();
            }
        }
        mainPanel.revalidate();
        mainPanel.repaint();
    }

    private void criarTela(JPanel center, JPanel botton) {
        mainPanel.add(center, BorderLayout.CENTER);
        mainPanel.add(botton, BorderLayout.SOUTH);

        mainPanel.revalidate();
        mainPanel.repaint();
    }

    public void mudarTela(String tela) {
        cadastroPontoButton.removeActionListener(cadastroPontoActionListener);
        edicaoPontoButton.removeActionListener(edicaoPontoActionListener);
        exclusaoPontoButton.removeActionListener(exclusaoPontoActionListener);

        cadastroSegmentoButton.removeActionListener(cadastroSegmentoActionListener);
        edicaoSegmentoButton.removeActionListener(edicaoSegmentoActionListener);
        exclusaoSegmentoButton.removeActionListener(exclusaoSegmentoActionListener);

        switch (tela) {
            case "cadastro-ponto":
                limparTela();
                cadastroPontoPanel.removeAll();

                cadastroPontoPanel.add(cadastroPontoNomeLabel);
                cadastroPontoPanel.add(cadastroPontoNomeField);
                cadastroPontoPanel.add(cadastroPontoObsLabel);
                cadastroPontoPanel.add(cadastroPontoObsField);
                for (int i = 2; i >= 0; i--) {
                    cadastroPontoPanel.add(new JLabel(""));
                    cadastroPontoPanel.add(new JLabel(""));
                }

                criarTela(cadastroPontoPanel, buttonsPanel);
                break;

            case "listar-pontos":
                limparTela();
                listagemPontoPanel.removeAll();

                String[] colunas = {"ID", "Nome", "Obs" };
                Object[][] dados = new Object[listaPontos.size()][colunas.length];
                for (int i = 0; i < listaPontos.size(); i++) {
                    Pontos ponto = listaPontos.get(i);
                    dados[i][0] = ponto.getId();
                    dados[i][1] = ponto.getNome();
                    dados[i][2] = ponto.getObs();
                }

                JTable table = new JTable(dados, colunas);
                JScrollPane scrollPane = new JScrollPane(table);
                listagemPontoPanel.add(scrollPane);

                criarTela(listagemPontoPanel, buttonsPanel);
                break;

            case "edicao-ponto":
                limparTela();
                edicaoPontoPanel.removeAll();

                edicaoPontoPanel.add(idPontoLabel);
                edicaoPontoPanel.add(idPontoField);
                edicaoPontoPanel.add(cadastroPontoNomeLabel);
                edicaoPontoPanel.add(cadastroPontoNomeField);
                edicaoPontoPanel.add(cadastroPontoObsLabel);
                edicaoPontoPanel.add(cadastroPontoObsField);
                for (int i = 1; i >= 0; i--) {
                    edicaoPontoPanel.add(new JLabel(""));
                    edicaoPontoPanel.add(new JLabel(""));
                }

                criarTela(edicaoPontoPanel, buttonsPanel);
                break;

            case "exclusao-ponto":
                limparTela();
                exclusaoPontoPanel.removeAll();

                exclusaoPontoPanel.add(idPontoLabel);
                exclusaoPontoPanel.add(idPontoField);
                for (int i = 3; i >= 0; i--) {
                    exclusaoPontoPanel.add(new JLabel(""));
                    exclusaoPontoPanel.add(new JLabel(""));
                }

                criarTela(exclusaoPontoPanel, buttonsPanel);
                break;

            case "cadastro-segmento":
                limparTela();
                cadastroSegmentoPanel.removeAll();

                cadastroSegmentoPanel.add(cadastroSegmentoPontoOrigemLabel);
                cadastroSegmentoPanel.add(cadastroSegmentoPontoOrigemField);
                cadastroSegmentoPanel.add(cadastroSegmentoPontoDestinoLabel);
                cadastroSegmentoPanel.add(cadastroSegmentoPontoDestinoField);
                cadastroSegmentoPanel.add(cadastroSegmentoDirecaoLabel);
                cadastroSegmentoPanel.add(cadastroSegmentoDirecaoField);
                cadastroSegmentoPanel.add(cadastroSegmentoDistanciaLabel);
                cadastroSegmentoPanel.add(cadastroSegmentoDistanciaField);
                cadastroSegmentoPanel.add(cadastroSegmentoObsLabel);
                cadastroSegmentoPanel.add(cadastroSegmentoObsField);

                criarTela(cadastroSegmentoPanel, buttonsPanel);
                break;

            case "listar-segmentos":
                limparTela();
                listagemPontoPanel.removeAll();

                String[] colunasSegmentos = {"ID", "Ponto Origem", "Ponto Destino", "Direcao", "Destino", "Obs" };
                Object[][] dadosSegmentos = new Object[listaSegmentos.size()][colunasSegmentos.length];
                for (int i = 0; i < listaSegmentos.size(); i++) {
                    Segmentos segmento = listaSegmentos.get(i);
                    dadosSegmentos[i][0] = segmento.getId();
                    dadosSegmentos[i][1] = segmento.getPonto_origem().get("name");
                    dadosSegmentos[i][2] = segmento.getPonto_destino().get("name");
                    dadosSegmentos[i][3] = segmento.getDirecao();
                    dadosSegmentos[i][4] = segmento.getDistancia();
                    dadosSegmentos[i][5] = segmento.getObs();
                }

                JTable tableSegmentos = new JTable(dadosSegmentos, colunasSegmentos);
                JScrollPane scrollPaneSegmentos = new JScrollPane(tableSegmentos);
                listagemPontoPanel.add(scrollPaneSegmentos);

                criarTela(listagemPontoPanel, buttonsPanel);
                break;

            case "edicao-segmento":
                limparTela();
                edicaoSegmentoPanel.removeAll();

                edicaoSegmentoPanel.add(idSegmentoLabel);
                edicaoSegmentoPanel.add(idSegmentoField);
                edicaoSegmentoPanel.add(cadastroSegmentoPontoOrigemLabel);
                edicaoSegmentoPanel.add(cadastroSegmentoPontoOrigemField);
                edicaoSegmentoPanel.add(cadastroSegmentoPontoDestinoLabel);
                edicaoSegmentoPanel.add(cadastroSegmentoPontoDestinoField);
                edicaoSegmentoPanel.add(cadastroSegmentoDirecaoLabel);
                edicaoSegmentoPanel.add(cadastroSegmentoDirecaoField);
                edicaoSegmentoPanel.add(cadastroSegmentoDistanciaLabel);
                edicaoSegmentoPanel.add(cadastroSegmentoDistanciaField);
                edicaoSegmentoPanel.add(cadastroSegmentoObsLabel);
                edicaoSegmentoPanel.add(cadastroSegmentoObsField);

                criarTela(edicaoSegmentoPanel, buttonsPanel);
                break;

            case "exclusao-segmento":
                limparTela();
                exclusaoSegmentoPanel.removeAll();

                exclusaoSegmentoPanel.add(idSegmentoLabel);
                exclusaoSegmentoPanel.add(idSegmentoField);
                for (int i = 3; i >= 0; i--) {
                    exclusaoSegmentoPanel.add(new JLabel(""));
                    exclusaoSegmentoPanel.add(new JLabel(""));
                }

                criarTela(exclusaoSegmentoPanel, buttonsPanel);
                break;

            default:
                JOptionPane.showMessageDialog(null, "Tela não reconhecida");
                break;
        }
    }

    public void updatePontosList(ArrayNode pontosNode) {
        ObjectMapper mapper = new ObjectMapper();
        listaPontos.clear();

        try {
            if (pontosNode != null) {
                for (JsonNode userNode : pontosNode) {
                    Pontos ponto = mapper.readValue(userNode.toString(), Pontos.class);
                    listaPontos.add(ponto);
                }
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public void updateSegmentosList(ArrayNode pontosNode) {
        ObjectMapper mapper = new ObjectMapper();
        listaSegmentos.clear();

        try {
            if (pontosNode != null) {
                for (JsonNode userNode : pontosNode) {
                    Segmentos segmento = mapper.readValue(userNode.toString(), Segmentos.class);
                    listaSegmentos.add(segmento);
                }
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public void updatePonto(JsonNode pontoNode) {
        currentPonto = pontoNode;
    }

    ActionListener exclusaoPontoActionListener = new ActionListener() {
        @Override
        public void actionPerformed(ActionEvent e) {
            String action = "excluir-ponto";
            ObjectMapper mapper = new ObjectMapper();
            ObjectNode data = mapper.createObjectNode();
            String token = cliente.getToken();
            Integer pontoId = Integer.parseInt(idPontoField.getText());

            data.put("ponto_id", pontoId);
            data.put("token", token);

            ObjectNode requestData = mapper.createObjectNode();
            requestData.put("action", action);
            requestData.set("data", data);
            System.out.println(requestData);

            try {
                String jsonString = mapper.writeValueAsString(requestData);
                cliente.sendRequestToServer(jsonString, action);
            } catch (JsonProcessingException ex) {
                ex.printStackTrace();
            }
        }
    };

    ActionListener edicaoPontoActionListener = new ActionListener() {
        @Override
        public void actionPerformed(ActionEvent e) {
            String action = "edicao-ponto";
            ObjectMapper mapper = new ObjectMapper();
            ObjectNode data = mapper.createObjectNode();
            String token = cliente.getToken();
            String nome = cadastroPontoNomeField.getText();
            String obs = cadastroPontoObsField.getText();

            data.put("name", nome);
            data.put("obs", obs);
            data.put("ponto_id", Integer.parseInt(idPontoField.getText()));
            data.put("token", token);

            ObjectNode requestData = mapper.createObjectNode();
            requestData.put("action", action);
            requestData.set("data", data);
            System.out.println(requestData);

            try {
                String jsonString = mapper.writeValueAsString(requestData);
                cliente.sendRequestToServer(jsonString, action);
            } catch (JsonProcessingException ex) {
                ex.printStackTrace();
            }
        }
    };

    ActionListener cadastroPontoActionListener = new ActionListener() {
        @Override
        public void actionPerformed(ActionEvent e) {
            String action = "cadastro-ponto";
            String nome = cadastroPontoNomeField.getText();
            String obs =  cadastroPontoObsField.getText();
            String token = cliente.getToken();

            ObjectMapper mapper = new ObjectMapper();
            ObjectNode cadastroData = mapper.createObjectNode();


            ObjectNode data = mapper.createObjectNode();
            data.put("name", nome);
            data.put("obs", obs);
            data.put("token", token);

            cadastroData.put("action", action);
            cadastroData.set("data", data);

            try {
                String jsonString = mapper.writeValueAsString(cadastroData);

                cliente.sendRequestToServer(jsonString, action);
                SwingUtilities.invokeLater(() -> {
                    frame.requestFocusInWindow();
                });
            } catch (IOException ex) {
                ex.printStackTrace();
            }
        };
    };

    ActionListener cadastroSegmentoActionListener = new ActionListener() {
        @Override
        public void actionPerformed(ActionEvent e) {
            String action = "cadastro-segmento";
            String token = cliente.getToken();
            String ponto_origem = cadastroSegmentoPontoOrigemField.getText();
            String ponto_destino = cadastroSegmentoPontoDestinoField.getText();
            String direcao = cadastroSegmentoDirecaoField.getText();
            String distancia = cadastroSegmentoDistanciaField.getText();
            String obs = cadastroSegmentoObsField.getText();
            ObjectMapper mapper = new ObjectMapper();
            ObjectNode data = mapper.createObjectNode();

            ObjectNode cadastroData = mapper.createObjectNode();

            getPonto(Integer.parseInt(ponto_origem), token);
            if (currentPonto == null) {
                return;
            }
            data.putIfAbsent("ponto_origem", currentPonto.get(0));
            currentPonto = null;
            getPonto(Integer.parseInt(ponto_destino), token);
            if (currentPonto == null) {
                return;
            }
            data.putIfAbsent("ponto_destino", currentPonto.get(0));
            currentPonto = null;

            data.put("token", token);
            data.put("direcao", direcao);
            data.put("distancia", Integer.parseInt(distancia));
            data.put("obs", obs);

            cadastroData.put("action", action);
            cadastroData.set("data", data);

            try {
                String jsonString = mapper.writeValueAsString(cadastroData);

                cliente.sendRequestToServer(jsonString, action);
                SwingUtilities.invokeLater(() -> {
                    frame.requestFocusInWindow();
                });
            } catch (IOException ex) {
                ex.printStackTrace();
            }
        };
    };

    ActionListener edicaoSegmentoActionListener = new ActionListener() {
        @Override
        public void actionPerformed(ActionEvent e) {
            String action = "edicao-segmento";
            String token = cliente.getToken();
            String ponto_origem = cadastroSegmentoPontoOrigemField.getText();
            String ponto_destino = cadastroSegmentoPontoDestinoField.getText();
            String direcao = cadastroSegmentoDirecaoField.getText();
            String distancia = cadastroSegmentoDistanciaField.getText();
            String obs = cadastroSegmentoObsField.getText();
            ObjectMapper mapper = new ObjectMapper();
            ObjectNode data = mapper.createObjectNode();

            ObjectNode cadastroData = mapper.createObjectNode();

            getPonto(Integer.parseInt(ponto_origem), token);
            if (currentPonto == null) {
                return;
            }
            data.putIfAbsent("ponto_origem", currentPonto.get(0));
            currentPonto = null;
            getPonto(Integer.parseInt(ponto_destino), token);
            if (currentPonto == null) {
                return;
            }
            data.putIfAbsent("ponto_destino", currentPonto.get(0));
            currentPonto = null;

            data.put("token", token);
            data.put("direcao", direcao);
            data.put("distancia", Integer.parseInt(distancia));
            data.put("obs", obs);
            data.put("segmento_id", Integer.parseInt(idSegmentoField.getText()));

            cadastroData.put("action", action);
            cadastroData.set("data", data);

            try {
                String jsonString = mapper.writeValueAsString(cadastroData);

                cliente.sendRequestToServer(jsonString, action);
                SwingUtilities.invokeLater(() -> {
                    frame.requestFocusInWindow();
                });
            } catch (IOException ex) {
                ex.printStackTrace();
            }
        };
    };

    ActionListener exclusaoSegmentoActionListener = new ActionListener() {
        @Override
        public void actionPerformed(ActionEvent e) {
            String action = "excluir-segmento";
            ObjectMapper mapper = new ObjectMapper();
            ObjectNode data = mapper.createObjectNode();
            String token = cliente.getToken();
            Integer segmentoId = Integer.parseInt(idSegmentoField.getText());

            data.put("segmento_id", segmentoId);
            data.put("token", token);

            ObjectNode requestData = mapper.createObjectNode();
            requestData.put("action", action);
            requestData.set("data", data);

            try {
                String jsonString = mapper.writeValueAsString(requestData);
                cliente.sendRequestToServer(jsonString, action);
            } catch (JsonProcessingException ex) {
                ex.printStackTrace();
            }
        }
    };

    private void getPonto(int pontoId, String token) {
        String action = "pedido-edicao-ponto";
        ObjectMapper mapper = new ObjectMapper();
        ObjectNode segmentoData = mapper.createObjectNode();

        ObjectNode data = mapper.createObjectNode();
        data.put("token", token);
        data.put("ponto_id", pontoId);

        segmentoData.put("action", action);
        segmentoData.set("data", data);

        try {
            String jsonString = mapper.writeValueAsString(segmentoData);

            cliente.sendRequestToServer(jsonString, action);
        } catch (IOException ex) {
            ex.printStackTrace();
        }
    }

    public void show() {
        frame.setVisible(true);
    }

    public void hide() {
        frame.setVisible(false);
    }
}
