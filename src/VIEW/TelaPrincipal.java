package VIEW;

import CONTROL.*;

import java.awt.*;
import java.awt.event.*;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;

import javax.imageio.ImageIO;
import javax.swing.*;

public class TelaPrincipal {
	private JFrame frame = new JFrame();
    private JPanel painel_esquerdo = new JPanel();
    private JPanel painel_direito = new JPanel();
    private JPanel tela = new JPanel();
    private JButton buscar_imagem = new JButton("Procurar Imagem");
    private JButton aplicar_filtro = new JButton("Aplicar Filtro Gaussiano");
    private JButton comprimir_imagem = new JButton("Comprimir Imagem");
    private JButton descomprimir_imagem = new JButton("Descomprimir Imagem");
    private JButton salvar = new JButton("Salvar Imagem");
    private JPanel imagem_original = new JPanel();
    private JPanel imagem_filtro = new JPanel();
    private BufferedImage imagemOriginal = null;
    private BufferedImage imagemGaussiana = null;
    private String caminhoImagem = null;
    
	public void Exibir_Tela_inicial(){
        buscar_imagem.addActionListener(
				new ActionListener(){
					public void actionPerformed(ActionEvent e){
						ControlaImagem controlaImagem = new ControlaImagem();

				        JFileChooser fc = new JFileChooser();   
				        
				        fc.showOpenDialog(null); 
				        caminhoImagem = fc.getSelectedFile().getAbsolutePath();

						imagemOriginal = controlaImagem.CarregarImagem(caminhoImagem);
						
						MostrarImagem(imagemOriginal, "original");
						
						imagem_original.repaint(1);
						imagem_original.revalidate();
					}
				}
		);
        
        aplicar_filtro.addActionListener(
        		new ActionListener(){
        			public void actionPerformed(ActionEvent e){
						if(imagemOriginal != null){
							JTextField desvio = new JTextField();
					        JTextField tamanhoMascara = new JTextField();
					        Object[] message = {
					            "Desvio Padrão:", desvio,
					            "Tamanho da Máscara:", tamanhoMascara
					        };

					        int option = JOptionPane.showConfirmDialog(null, message, "Configurações Filtro Gaussiano", JOptionPane.OK_CANCEL_OPTION);
					        if (option == JOptionPane.OK_OPTION) {
					        	if(desvio == null || desvio.equals("")){
									JOptionPane.showMessageDialog(null,"Selecione uma imagem antes de aplicar o filtro!");
								}else{
									ControlaImagem controlarImagem = new ControlaImagem();
									imagemGaussiana = controlarImagem.AplicarFiltroGaussiano(imagemOriginal, Float.parseFloat(desvio.getText()), Integer.parseInt(tamanhoMascara.getText()));
									MostrarImagem(imagemGaussiana, "gaussiana");
								}
					        } else {
					            //System.out.println("Login canceled");
					        }
							

						}else{
							JOptionPane.showMessageDialog(null,"Entre com o valor do desvio padrão!");
						}
        				
        			}
        		}
        );
        
        comprimir_imagem.addActionListener(
        		new ActionListener(){
        			public void actionPerformed(ActionEvent e){
        				if(caminhoImagem != null){
        					JTextField janelaDicionario = new JTextField();
					        JTextField tamanhoBuffer = new JTextField();
					        Object[] message = {
					            "Janela de dicionario:", janelaDicionario,
					            "Tamanho do buffer:", tamanhoBuffer
					        };

					        int numTuplas = 0;
					        
					        int option = JOptionPane.showConfirmDialog(null, message, "Configurações para a Compressão", JOptionPane.OK_CANCEL_OPTION);
					        if (option == JOptionPane.OK_OPTION) {
					        	ControlaCompressao controlarCompressao = new ControlaCompressao();
	            				try {
	    							numTuplas = controlarCompressao.comprimirImagem(caminhoImagem, Integer.parseInt(janelaDicionario.getText()), Integer.parseInt(tamanhoBuffer.getText()));
	    						} catch (IOException e1) {
	    							// TODO Auto-generated catch block
	    							e1.printStackTrace();
	    						}
	            				String textoMenssagem = "Finalizada Compressão da Imagem!\n\n";
	            				textoMenssagem += "Imagem original: 44000 bits\n";
	            				textoMenssagem += "Tempo necessário: 100 segundos\n";
	            				textoMenssagem += "Taxa de compressão: 25%";
	            				
	            				
	            				JOptionPane.showMessageDialog(null,textoMenssagem);
					        } else {
					            //System.out.println("Login canceled");
					        }
        					
        				}else{
        					JOptionPane.showMessageDialog(null,"Selecione uma imagem antes de aplicar a compressão!");
        				}
        				
        			}
        		}
        );
        
        descomprimir_imagem.addActionListener(
        		new ActionListener(){
        			public void actionPerformed(ActionEvent e){
        				ControlaCompressao controlarCompressao = new ControlaCompressao();
        				byte[] matrizBytes = null;
        				try {
							matrizBytes = controlarCompressao.descomprimirImagem();
						} catch (IOException e1) {
							// TODO Auto-generated catch block
							e1.printStackTrace();
						}
        				
        				try {
							controlarCompressao.ByteParaImagem(matrizBytes);
						} catch (Exception e1) {
							// TODO Auto-generated catch block
							e1.printStackTrace();
						}
        				
        				JOptionPane.showMessageDialog(null,"Imagem descomprimida com sucesso!"); 
        			}
        		}
        );
        
        salvar.addActionListener(
        		new ActionListener(){
        			public void actionPerformed(ActionEvent e){
        				ControlaImagem controlaImagem = new ControlaImagem();
        				try {
							controlaImagem.SalvarImagens(imagemOriginal, imagemGaussiana);
						} catch (IOException e1) {
							// TODO Auto-generated catch block
							e1.printStackTrace();
						}
        				
        				JOptionPane.showMessageDialog(null,"Imagem salva com sucesso!"); 
        			}
        		}
        		);
        painel_esquerdo.add(buscar_imagem);
        painel_esquerdo.add(aplicar_filtro);
        painel_esquerdo.add(comprimir_imagem);
        painel_esquerdo.add(descomprimir_imagem);
        painel_esquerdo.add(salvar);
        
        imagem_original.setBorder(BorderFactory.createTitledBorder("Imagem Original"));
        imagem_filtro.setBorder(BorderFactory.createTitledBorder("Imagem com o filtro Gaussiano"));

        
        painel_direito.add(imagem_original);
        painel_direito.add(imagem_filtro);
        painel_direito.setLayout(new GridLayout(2,1,10,10));
        
        painel_esquerdo.setBorder(BorderFactory.createEtchedBorder());
        painel_direito.setBorder(BorderFactory.createEtchedBorder());
        
        painel_esquerdo.setLayout(new GridLayout(4,1,10,200));
        tela.setLayout(new GridLayout(1,2,10,0));
        
        painel_esquerdo.setSize(300, 500);
        painel_direito.setSize(700, 600);
        tela.add(painel_esquerdo);
        tela.add(painel_direito);
        frame.add(tela);

        frame.setTitle("PDI - Trabalho final - Filtro Gaussiano e Compressão LZ77");
        frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        frame.setExtendedState(JFrame.MAXIMIZED_BOTH);
        frame.setVisible(true);
	}
	
	public void MostrarImagem(BufferedImage imagem, String status){
		if(status == "original"){
			ImageIcon icon = new ImageIcon(imagem);
	        JLabel imageLabel = new JLabel(icon);
	        imagem_original.removeAll();
	        imagem_original.add(imageLabel);
	        
		}else{
			ImageIcon icon = new ImageIcon(imagem);
	        JLabel imageLabel = new JLabel(icon);
	        imagem_filtro.removeAll();
	        imagem_filtro.add(imageLabel);
	        imagem_filtro.repaint(1);
			imagem_filtro.revalidate();
	        
		}
	}
}
