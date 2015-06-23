package CONTROL;

import java.awt.image.BufferedImage;
import java.awt.image.ColorModel;
import java.awt.image.Kernel;

public class FiltroGaussiano {

	public final static float PI = (float)Math.PI;
	
	public static int ZERO_EDGES = 0;
	public static int CLAMP_EDGES = 1;
	public static int WRAP_EDGES = 2;

	protected Kernel kernel = null;
	public boolean alpha = true;
	protected float desvioPadrao;

	private int tamanhoMascara;

	
	//Construtores
	public FiltroGaussiano() {
		this(1,1);
	}

	public FiltroGaussiano(float desvioPadrao, int tamanhoMascara) {
		setdesvioPadrao(desvioPadrao, tamanhoMascara);
	}


	public void setdesvioPadrao(float desvioPadrao, int tamanhoMascara) {
		this.desvioPadrao = desvioPadrao;
		this.tamanhoMascara = tamanhoMascara;
		kernel = criarMascara(desvioPadrao, tamanhoMascara);
	}
	

	public float getdesvioPadrao() {
		return desvioPadrao;
	}

	//Aplicar o filtro Gaussiano em uma imagem construida a partir da Imagem original.
    public BufferedImage filter( BufferedImage imagemOriginal) {
        int comprimento = imagemOriginal.getWidth();
        int altura = imagemOriginal.getHeight();

        
        BufferedImage imagemGaussiana = criarImagemDestino( imagemOriginal, null );

        int[] inPixels = new int[comprimento*altura];
        int[] outPixels = new int[comprimento*altura];
        imagemOriginal.getRGB( 0, 0, comprimento, altura, inPixels, 0, comprimento );

        convolucaoETranposicao(kernel, inPixels, outPixels, comprimento, altura, alpha, CLAMP_EDGES);
		convolucaoETranposicao(kernel, outPixels, inPixels, altura, comprimento, alpha, CLAMP_EDGES);

        imagemGaussiana.setRGB( 0, 0, comprimento, altura, inPixels, 0, comprimento );
        return imagemGaussiana;
    }

    //Criar uma imagem semelhante a original para que seja aplicado o filtro gaussiano, sem alterações na imagem Original.
    public BufferedImage criarImagemDestino(BufferedImage imagemOriginal, ColorModel dstCM) {

        dstCM = imagemOriginal.getColorModel();
        return new BufferedImage(dstCM, dstCM.createCompatibleWritableRaster(imagemOriginal.getWidth(), imagemOriginal.getHeight()), dstCM.isAlphaPremultiplied(), null);
    }
    
	public static void convolucaoETranposicao(Kernel kernel, int[] inPixels, int[] outPixels, int comprimento, int altura, boolean alpha, int edgeAction) {
		float[] matrix = kernel.getKernelData( null );
		int cols = kernel.getWidth();
		int cols2 = cols/2;

		for (int y = 0; y < altura; y++) {
			int index = y;
			int ioffset = y*comprimento;
			for (int x = 0; x < comprimento; x++) {
				float r = 0, g = 0, b = 0, a = 0;
				int moffset = cols2;
				for (int col = -cols2; col <= cols2; col++) {
					float f = matrix[moffset+col];

					if (f != 0) {
						int ix = x+col;
						if ( ix < 0 ) {
							if ( edgeAction == CLAMP_EDGES )
								ix = 0;
							else if ( edgeAction == WRAP_EDGES )
								ix = (x+comprimento) % comprimento;
						} else if ( ix >= comprimento) {
							if ( edgeAction == CLAMP_EDGES )
								ix = comprimento-1;
							else if ( edgeAction == WRAP_EDGES )
								ix = (x+comprimento) % comprimento;
						}
						int rgb = inPixels[ioffset+ix];
						a += f * ((rgb >> 24) & 0xff);
						r += f * ((rgb >> 16) & 0xff);
						g += f * ((rgb >> 8) & 0xff);
						b += f * (rgb & 0xff);
					}
				}
				int ia = alpha ? adequarPixel((int)(a+0.5)) : 0xff;
				int ir = adequarPixel((int)(r+0.5));
				int ig = adequarPixel((int)(g+0.5));
				int ib = adequarPixel((int)(b+0.5));
				outPixels[index] = (ia << 24) | (ir << 16) | (ig << 8) | ib;
                index += altura;
			}
		}
	}
	
	//Adequação das cores caso esteja fora do range 0~255
	public static int adequarPixel(int c) {
		if (c < 0)
			return 0;
		if (c > 255)
			return 255;
		return c;
	}

	//Criar mascara gaussiana
	public static Kernel criarMascara(float desvioPadrao, int tamanhoMascara) {
		//int r = (int)Math.ceil(desvioPadrao);
		int r = tamanhoMascara;
		int rows = r*2+1;
		float[] matrix = new float[rows];
		float sigma = desvioPadrao/3;
		float sigma22 = 2*sigma*sigma;
		float sigmaPi2 = 2*PI*sigma;
		float sqrtSigmaPi2 = (float)Math.sqrt(sigmaPi2);
		float desvioPadrao2 = desvioPadrao*desvioPadrao;
		float total = 0;
		int index = 0;
		for (int row = -r; row <= r; row++) {
			float distance = row*row;
			if (distance > desvioPadrao2)
				matrix[index] = 0;
			else
				matrix[index] = (float)Math.exp(-(distance)/sigma22) / sqrtSigmaPi2;
			total += matrix[index];
			index++;
		}
		for (int i = 0; i < rows; i++)
			matrix[i] /= total;

		return new Kernel(rows, 1, matrix);
	}

}
