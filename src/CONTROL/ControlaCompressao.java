package CONTROL;

import java.io.BufferedReader;
import java.io.FileDescriptor;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.FileWriter;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.PrintWriter;

public class ControlaCompressao {
	  byte[] rawData;
	  int matchLen;
	  int matchLoc;
	  int charCnt;
	  int tamanhoJanelaBusca;
	  int tamanhoFinalBuffer;
	  int contadorJanela;
	  int caracteresJanela;
	  int contadorRetornoJanela = 0;
	  int contadorColetorCaracteres = 0;
	  int contador = 0;
	  int posicao = 0;
	  int searchWindowLen;
	  int tamanhoBufferLen;
	  
	  public void comprimirImagem(String caminhoArquivo, int tamanhoJanela, int tamanhoBuffer) throws IOException{		   
		        searchWindowLen = tamanhoJanela;
		        tamanhoBufferLen = tamanhoBuffer;
		        
		        byte[] imagemBytes = imagemParaByte(caminhoArquivo);
		        
		     
		        System.out.println(imagemBytes);
		       
		        rawData = imagemBytes;
	    
		    FileWriter arq = new FileWriter("compressao/imagem_original_comprimida.txt");
		    PrintWriter gravarArq = new PrintWriter(arq);
		      

		    charCnt = 0;
		    contadorJanela = 0;
		    caracteresJanela = 0;
		    while(charCnt < rawData.length){

		       if(caracteresJanela > 0){
		           if(caracteresJanela <= searchWindowLen){
		               for(int j=0; j < caracteresJanela; j++){
		                   //gravarArq.printf("%s", rawData[j]+",");
		                   //System.out.print(rawData[j]+",");
		               }
		               caracteresJanela++;
		           }else{
		               for(int j=charCnt - searchWindowLen; j < charCnt; j++){
		                   //gravarArq.printf("%s", rawData[j]+",");
		                   //System.out.print(rawData[j]+",");
		               }         
		           }
		       }else{
		           //gravarArq.print(" ");
		           //System.out.print(" ");
		           
		           caracteresJanela++;
		       }
		       //gravarArq.print(" | ");
		       //System.out.print(" | ");
		       
		       if(rawData.length - charCnt >= tamanhoBufferLen){
		           for(int i=0; i < tamanhoBufferLen; i++){
		                 //gravarArq.printf("%s", rawData[charCnt+i]+",");
		                 //System.out.print(rawData[charCnt+i]+",");
		            }
		       }else{
		           for(int i=0; i < rawData.length-charCnt; i++){
		                 //gravarArq.printf("%s", rawData[charCnt+i]+",");
		                 //System.out.print(rawData[charCnt+i]+",");
		           }
		       }
		       
		       if(caracteresJanela > 1){
		           int auxContadorCaracteres = 0;
		           int auxContadorRetornoJanela = 0;
		           for(int i = charCnt-1; i >= charCnt-(caracteresJanela-1); i--){
		               contador++;
		               if(rawData[charCnt] == rawData[i]){
		            	   auxContadorRetornoJanela = contador;
		                   auxContadorCaracteres++;
		                   posicao = i;
		                   for(int k = (posicao + 1); k < (posicao+1)+tamanhoBufferLen; k++){
		                		   int cont = 1;
		                		   if(cont == 1){
		                			   if(rawData[k] == rawData[charCnt + cont]){
		                				   auxContadorCaracteres++;
		                			   }else{
		                				   if(rawData[k] == rawData[charCnt + cont] && rawData[k-1] == rawData[(charCnt + (cont-1))]){
		                					   auxContadorCaracteres++;
		                				   }
		                			   }
		                		   }
		                   }
		               }
		               if(auxContadorCaracteres > contadorColetorCaracteres){
		            	   contadorColetorCaracteres = auxContadorCaracteres;
		            	   contadorRetornoJanela = auxContadorRetornoJanela;
		               }
		               auxContadorCaracteres = 0;
			           auxContadorRetornoJanela = 0;
		           }

		           if(contadorRetornoJanela > 0){
		               gravarArq.printf("%d,%d,%s", contadorRetornoJanela, contadorColetorCaracteres, rawData[charCnt+(contadorColetorCaracteres-1)]);
		               //System.out.printf(" | (%d,%d,%s)", contadorRetornoJanela, contadorColetorCaracteres, rawData[charCnt+(contadorColetorCaracteres-1)]);
		           }else{
		               gravarArq.printf("%d,%d,%s", contadorRetornoJanela, contadorColetorCaracteres, rawData[charCnt]);
		               //System.out.printf(" | (%d,%d,%s)", contadorRetornoJanela, contadorColetorCaracteres, rawData[charCnt]);
		           }
		          
		           
		       }else{
		           gravarArq.printf("0,0,%s", rawData[charCnt]);
		           //System.out.printf(" | (0,0,%s)", rawData[charCnt]);
		       }
		           
		           gravarArq.printf("%n");
		           //System.out.println();
		        
		      if(contadorColetorCaracteres == 0){
		          charCnt++;
		      }else{
		          charCnt = charCnt + contadorColetorCaracteres-1;
		          charCnt++;
		          if(caracteresJanela < searchWindowLen){
		              if(caracteresJanela + contadorColetorCaracteres > searchWindowLen){
		                  caracteresJanela = searchWindowLen;
		              }else{
		                  caracteresJanela += contadorColetorCaracteres-1;
		              }
		          }
		      }
		      
		    contador = 0;
		    posicao = 0;
		    contadorRetornoJanela = 0;
		    contadorColetorCaracteres = 0;

		    }//end while loop
		arq.close();
	  }
	  
	  public byte[] descomprimirImagem() throws IOException{

		  int contadorMatriz = 0;
		  FileInputStream stream = new FileInputStream("compressao/imagem_original_comprimida.txt");
		  InputStreamReader reader = new InputStreamReader(stream);
		  BufferedReader br = new BufferedReader(reader);
		  String linha = br.readLine();
		  
		  int matrizLenght = 0;
		  int posicoesRepetidas = 0;
		  while(linha != null){
			  int valoresASeremAdicionado = Integer.parseInt(linha.substring(linha.indexOf(',') + 1, linha.lastIndexOf(',')));
			  posicoesRepetidas += valoresASeremAdicionado;
			  matrizLenght++;
			  linha = br.readLine();
		  }
		  
		  matrizLenght += posicoesRepetidas;

		  BufferedReader br2 = new BufferedReader(reader);
		  String linha2 = br2.readLine();
		  
		  byte[] matrizImagem = new byte[matrizLenght];
		  
		  while(linha2 != null){
			  
			   int valorRetornoPosicoes = Integer.parseInt(linha2.substring(0, linha2.indexOf(',')));
			   int valoresASeremAdicionado = Integer.parseInt(linha2.substring(linha2.indexOf(',') + 1, linha2.lastIndexOf(',')));
			   byte valorASerAdicionadoPorUltimo = Byte.parseByte(linha2.substring(linha2.lastIndexOf(',') + 1, linha2.length()));
			   if(valorRetornoPosicoes == 0){
				   matrizImagem[contadorMatriz] = valorASerAdicionadoPorUltimo;
				   contadorMatriz++;
			   }else{
				   int contadorAux = contadorMatriz;
				   for(int i = contadorMatriz - valorRetornoPosicoes; i < (contadorMatriz - valorRetornoPosicoes)+valoresASeremAdicionado; i++){
					   matrizImagem[contadorAux] = matrizImagem[i];
					   contadorAux++;
				   }
				   matrizImagem[contadorAux+1] = valorASerAdicionadoPorUltimo;
				   contadorMatriz += contadorAux;
			   }

			  linha = br.readLine();
		  }
		  
		  return matrizImagem;
	  }
	  
	  public byte[] imagemParaByte(String image) throws IOException {
	        InputStream is = null;
	        byte[] buffer = null;
	        is = new FileInputStream(image);
	        buffer = new byte[is.available()];
	        is.read(buffer);
	        is.close();
	        return buffer;
	    } 
	    
	    public void ByteParaImagem(byte[] bytes) throws Exception {
	        byte[] imgBytes = bytes;
	        try {
	            FileOutputStream fos = new FileOutputStream("imagens/java.jpg");
	            fos.write(imgBytes);
	            FileDescriptor fd = fos.getFD();
	            fos.flush();
	            fd.sync();
	            fos.close();
	        } catch (Exception e) {
	                throw new Exception("Erro ao converter os bytes recebidos para imagem");
	        }
	    }
}
