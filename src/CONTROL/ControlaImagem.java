package CONTROL;

import VIEW.*;

import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;

import javax.imageio.ImageIO;
import javax.swing.JFileChooser;

public class ControlaImagem {

	public BufferedImage CarregarImagem(String caminhoImagem){
		BufferedImage original = null;
		try {
 
            original = ImageIO.read(new File(caminhoImagem));

        }
        catch(IOException e){
            System.out.println("Erro! Verifique se o arquivo especificado existe e tente novamente.");
        }
        catch(Exception e){
            System.out.println("Erro! " + e.getMessage());
        }
		return original;
	}
	
	public BufferedImage AplicarFiltroGaussiano(BufferedImage imagemOriginal, float desvioPadrao, int tamanhoMascara){
		FiltroGaussiano op = new FiltroGaussiano(desvioPadrao, tamanhoMascara);
		
		BufferedImage imagemGaussiana = op.filter(imagemOriginal);
		
		return imagemGaussiana;
			
	}
	
	public void SalvarImagens(BufferedImage original, BufferedImage gaussiana) throws IOException{
		JFileChooser salvarFC = new JFileChooser();
		String caminhoArquivo = "";
		//String nomeArquivo = "";

		salvarFC.showSaveDialog(null);
		caminhoArquivo = salvarFC.getSelectedFile().getAbsolutePath();
		//nomeArquivo = salvarFC.getSelectedFile().getName();

		ImageIO.write(gaussiana, "jpg", new File(caminhoArquivo+".jpg"));
		
		/*if(original != null){
			ImageIO.write(original,"jpg",new File("imagens/imagem_original.jpg"));
		}
		if(original != null && gaussiana != null){
			ImageIO.write(gaussiana, "jpg", new File("imagens/imagem_gaussiana.jpg"));
		}*/
	}
	
}
