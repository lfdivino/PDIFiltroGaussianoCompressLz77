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
	  
	  public int[] comprimirImagem(int tamanhoJanela, int tamanhoBuffer) throws IOException{		   
		  String caminhoArquivo = "imagens/imagem_filtro.jpg";      
		  searchWindowLen = tamanhoJanela;
		        tamanhoBufferLen = tamanhoBuffer;
		        int[] dadosCompressao = new int[2];
		        dadosCompressao[1] = 0;
		        
		        byte[] imagemBytes = imagemParaByte(caminhoArquivo);
		        
		     
		        System.out.println(imagemBytes);
		       
		        rawData = imagemBytes;
	    
		    FileWriter arq = new FileWriter("compressao/imagem_comprimida.txt");
		    PrintWriter gravarArq = new PrintWriter(arq);
		      

		    charCnt = 0;
		    contadorJanela = 0;
		    caracteresJanela = 0;
		    dadosCompressao[0] = rawData.length;

		    while(charCnt < rawData.length){

		      if(caracteresJanela > 0){
		           if(caracteresJanela <= searchWindowLen){
		               /*for(int j=0; j < caracteresJanela; j++){
		                   //gravarArq.printf("%s", rawData[j]+",");
		                   System.out.print(rawData[j]+",");
		               }*/
		               caracteresJanela++;
		           }else{
		               /*for(int j=charCnt - searchWindowLen; j < charCnt; j++){
		                   //gravarArq.printf("%s", rawData[j]+",");
		                   System.out.print(rawData[j]+",");
		               }*/         
		           }
		       }else{
		           //gravarArq.print(" ");
		           //System.out.print(" ");
		           
		           caracteresJanela++;
		       }
		       //gravarArq.print(" | ");
		       //System.out.print(" | ");
		       
		       /*if(rawData.length - charCnt >= tamanhoBufferLen){
		           for(int i=0; i < tamanhoBufferLen; i++){
		                 //gravarArq.printf("%s", rawData[charCnt+i]+",");
		                 System.out.print(rawData[charCnt+i]+",");
		            }
		       }else{
		           for(int i=0; i < rawData.length-charCnt; i++){
		                 //gravarArq.printf("%s", rawData[charCnt+i]+",");
		                 System.out.print(rawData[charCnt+i]+",");
		           }
		       }*/
		       
		       if(caracteresJanela > 1){
		           int auxContadorCaracteres = 0;
		           int auxContadorRetornoJanela = 0;
		           for(int i = charCnt-1; i >= charCnt-(caracteresJanela-1); i--){
		               contador++;
		               if(rawData[charCnt] == rawData[i]){
		            	   auxContadorRetornoJanela = contador;
		                   auxContadorCaracteres++;
		                   posicao = i;
		                   int cont = 1;
		                   if((posicao + 1) < charCnt){
		                	   for(int k = (posicao + 1); k < (posicao+1)+tamanhoBufferLen; k++){
		                		   if(k < rawData.length){
	                				   if(rawData[k] == rawData[charCnt + cont] && rawData[k-1] == rawData[(charCnt + (cont-1))] && auxContadorRetornoJanela > 1 && k < charCnt){
	                					   auxContadorCaracteres++;
	                				   }else{
	                					   break;
	                				   }
		                		   }
		                		   cont++;
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
		               gravarArq.printf("%d,%d,%s", contadorRetornoJanela, contadorColetorCaracteres, rawData[charCnt+(contadorColetorCaracteres)]);
		               //System.out.printf(" | (%d,%d,%s)", contadorRetornoJanela, contadorColetorCaracteres, rawData[charCnt+(contadorColetorCaracteres)]);
		               dadosCompressao[1]++;
		           }else{
		               gravarArq.printf("%d,%d,%s", contadorRetornoJanela, contadorColetorCaracteres, rawData[charCnt]);
		               //System.out.printf(" | (%d,%d,%s)", contadorRetornoJanela, contadorColetorCaracteres, rawData[charCnt]);
		               dadosCompressao[1]++;
		           }
		          
		           
		       }else{
		           gravarArq.printf("0,0,%s", rawData[charCnt]);
		           //System.out.printf(" | (0,0,%s)", rawData[charCnt]);
		           dadosCompressao[1]++;
		       }
		           
		           gravarArq.printf("%n");
		           //System.out.println();
		        
		      if(contadorColetorCaracteres == 0){
		          charCnt++;
		      }else{
		          charCnt = charCnt + contadorColetorCaracteres;
		          charCnt++;
		          if(caracteresJanela < searchWindowLen){
		              if(caracteresJanela + contadorColetorCaracteres > searchWindowLen){
		                  caracteresJanela = searchWindowLen;
		              }else{
		                  caracteresJanela += contadorColetorCaracteres;
		              }
		          }
		      }
		      
		    contador = 0;
		    posicao = 0;
		    contadorRetornoJanela = 0;
		    contadorColetorCaracteres = 0;

		    	//gravarArq.printf("0,0,%s", rawData[charCnt]);
		    	//gravarArq.printf("%n");
		    	//charCnt++;
		    }//end while loop
		arq.close();
		return dadosCompressao;
	  }
	  
	  public byte[] descomprimirImagem() throws IOException{

		  int contadorMatriz = 0;
		  FileInputStream stream = new FileInputStream("compressao/imagem_comprimida.txt");
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
		  
		  br.close();
		  
		  matrizLenght += posicoesRepetidas;
		  //System.out.println(matrizLenght);

		  FileInputStream stream2 = new FileInputStream("compressao/imagem_comprimida.txt");
		  InputStreamReader reader2 = new InputStreamReader(stream2);
		  BufferedReader br2 = new BufferedReader(reader2);
		  String linha2 = br2.readLine();
		  
		  byte[] matrizImagem = new byte[matrizLenght];
		  
		  while(linha2 != null){
			  
			   int valorRetornoPosicoes = Integer.parseInt(linha2.substring(0, linha2.indexOf(',')));
			   int valoresASeremAdicionado = Integer.parseInt(linha2.substring(linha2.indexOf(',') + 1, linha2.lastIndexOf(',')));
			   byte valorASerAdicionadoPorUltimo = Byte.parseByte(linha2.substring(linha2.lastIndexOf(',') + 1, linha2.length()));
			   //System.out.println(valorASerAdicionadoPorUltimo);
			   if(valorRetornoPosicoes == 0){
				   matrizImagem[contadorMatriz] = valorASerAdicionadoPorUltimo;
				   contadorMatriz++;
			   }else{
				   int contadorAux = contadorMatriz;
				   for(int i = contadorMatriz - valorRetornoPosicoes; i < (contadorMatriz - valorRetornoPosicoes)+valoresASeremAdicionado; i++){
					   matrizImagem[contadorAux] = matrizImagem[i];
					   contadorAux++;
				   }
				   matrizImagem[contadorAux] = valorASerAdicionadoPorUltimo;
				   contadorMatriz = contadorAux+1;
				   contadorAux = 0;
			   }

			  linha2 = br2.readLine();
		  }
		  
		  br2.close();
		  
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
	            FileOutputStream fos = new FileOutputStream("compressao/imagem_descomprimida.jpg");
	            fos.write(imgBytes);
	            FileDescriptor fd = fos.getFD();
	            fos.flush();
	            fd.sync();
	            fos.close();
	        } catch (Exception e) {
	                throw new Exception("Erro ao converter os bytes recebidos para imagem");
	        }
	    }
	    
	    public int contagemBits(int tamanhoCampo){
	    	int numBits = 0;
	    	
	    	if(tamanhoCampo == 1){
	    		numBits = 1;
	    	}else if(tamanhoCampo <= 2){
	    		numBits = 2;
	    	}else if(tamanhoCampo > 2 && tamanhoCampo <= 4){
	    		numBits = 3;
	    	}else if(tamanhoCampo > 4 && tamanhoCampo <= 8){
	    		numBits = 4;
	    	}else if(tamanhoCampo > 8 && tamanhoCampo <= 16){
	    		numBits = 5;
	    	}else if(tamanhoCampo > 16 && tamanhoCampo <= 32){
	    		numBits = 6;
	    	}else if(tamanhoCampo > 32 && tamanhoCampo <= 64){
	    		numBits = 7;
	    	}else if(tamanhoCampo > 64 && tamanhoCampo <= 128){
	    		numBits = 8;
	    	}else if(tamanhoCampo > 128 && tamanhoCampo <= 256){
	    		numBits = 9;
	    	}else if(tamanhoCampo > 256 && tamanhoCampo <= 512){
	    		numBits = 10;
	    	}else if(tamanhoCampo > 512 && tamanhoCampo <= 1024){
	    		numBits = 11;
	    	}else if(tamanhoCampo > 1024 && tamanhoCampo <= 2048){
	    		numBits = 12;
	    	}
	    	
	    	return numBits;	    
	    }
	    
	    public float porcentagemCompressao(int[] dadosCompressao, int bitsJanela, int bitsBuffer){
    		float porcentagemCompressao = 0;
    		int totalBitsCompressao = (bitsJanela + bitsBuffer + 8)*dadosCompressao[1];
    		
    		porcentagemCompressao = dadosCompressao[0]*8;
    		porcentagemCompressao = porcentagemCompressao/totalBitsCompressao;
    		porcentagemCompressao = porcentagemCompressao*100;
    		
    		return porcentagemCompressao;
    	}
}
