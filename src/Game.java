/* To use this Demo, place it in a java project with the 
	FlexiblePictureExplorer class and the other classes
	from the AP picture labs. Create a folder under
	that project called Pictures and copy the images
	from the AP picture lab there.
 */
import javax.imageio.ImageIO;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.Image;
import java.awt.Rectangle;
import java.awt.geom.AffineTransform;
import java.awt.image.BufferedImage;
import java.awt.image.ImageObserver;
import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;


public class Game extends FlexiblePictureExplorer implements ImageObserver{
	Picture coverPic = new Picture("Pictures\\GameBackground.jpg");
	private boolean Picture1 = false;
	private boolean Picture2 = false;
	private boolean Picture3 = false;
	private boolean Picture4 = false;
	private boolean Picture5 = false;
	private boolean Picture6 = false;
	private boolean Picture7 = false;
	private boolean Picture8 = false;
	private static Picture[] pictures = new Picture[] {
			firstPuzzlePiece1(),
			secondPuzzlePiece(),
			thirdPuzzlePiece3(),
			fourthPuzzlePiece4(),
			fifthPuzzlePiece5(),
			sixthPuzzlePiece6(),
			seventhPuzzlePiece7(),
			eigthPuzzlePiece8() };
	private BufferedImage bufferedImage;
	public static final int PICTURE1 = 1;
	public static final int PICTURE2 = 2;
	public static final int PICTURE3 = 3;
	public static final int PICTURE4 = 4;
	public static final int PICTURE5 = 5;
	public static final int PICTURE6 = 6;
	public static final int PICTURE7 = 7;
	public static final int PICTURE8 = 8;
	//private int Check;

	public static final int COVERED = 1;
	public static final int REVEALED = 2;
	public static final int PERMANENTLY_REVEALED = 3;

	private final int imgHeight, imgWidth;
	private final int xTiles, yTiles;
	private final int tileWidth, tileHeight;
	private final List<Pixel> revealedLocations = new ArrayList<>();
	private final Picture[][] pictureGrid;
	private final int[][] statusGrid;
	private final Picture coverPicture;
	private int permanentlyRevealedCount = 0;
	private int turnsCounter = 0;


	public Game(Picture[] pictures, Picture coverPict) {
		this(pictures, coverPict, 400, 400, 4, 4);
	}

	public Game(Picture[] pictures, Picture coverPicture, int width,
			int height, int numColumns, int numRows) {
		super(new Picture(height, width));
		setTitle("Concentration");
		setTitle("Puzzle");

		imgHeight = height;
		imgWidth = width;
		xTiles = numColumns;
		yTiles = numRows;
		tileWidth = imgWidth / xTiles;
		tileHeight = imgHeight / yTiles;
		pictureGrid = new Picture[xTiles][yTiles];
		statusGrid = new int[xTiles][yTiles];
		this.coverPicture = scaleSquare(coverPicture);

		initGrid(pictures);
		updateImage();
	}

	private void initGrid(Picture[] pictures) {
		ArrayList<Picture> pictureChoices = new ArrayList<>();
		for (Picture pict : pictures) {
			Picture scaled = scaleSquare(pict); // Scale the picture to the appropriate size
			pictureChoices.add(scaled);
			//pictureChoices.add(scaled);
		}
		Collections.shuffle(pictureChoices); // Randomizes the order of the list
		for (int x = 0; x < xTiles; x++) {
			for (int y = 0; y < yTiles; y++) {
				pictureGrid[x][y] = pictureChoices.remove(0);
				statusGrid[x][y] = COVERED;
			}
		}
	}

	private Picture scaleSquare(Picture pict) {
		double xScale = tileWidth / ((double) pict.getWidth());
		double yScale = tileHeight / ((double) pict.getHeight());
		Picture scaled;
		Picture centered = new Picture(tileHeight, tileWidth);
		if (xScale < yScale) {
			scaled = pictureScale(pict, xScale, xScale);
			centered.copy(scaled, (tileHeight - scaled.getHeight()) / 2, 0);
		} else {
			scaled = pictureScale(pict, yScale, yScale);
			centered.copy(scaled, 0, (tileWidth - scaled.getWidth()) / 2);
		}
		return centered;
	}

	private Pixel clickedSquare(Pixel clickedPixel) {
		int x = clickedPixel.getX() / tileWidth;
		int y = clickedPixel.getY() / tileHeight;
		return new Pixel(null, x, y);
	}

	private Picture pictureForSquare(Pixel location) {
		return pictureGrid[location.getX()][location.getY()];
	}
	private boolean pixelEqual(Pixel onePixel,Pixel secondPixel ) {
		boolean Alpha = false;
		boolean Red = false;
		boolean Green = false;
		boolean Blue = false;


		if(onePixel.getAlpha() == secondPixel.getAlpha() ) {
			Alpha = true;
		}
		if(onePixel.getRed() == secondPixel.getRed() ) {
			Red = true;
		}
		if(onePixel.getGreen() == secondPixel.getGreen() ) {
			Green= true;
		}
		if(onePixel.getBlue() == secondPixel.getBlue() ) {
			Blue= true;
		}
		
		return (Alpha && Red && Green && Blue);
	}
	private boolean equals(Picture one ,Picture two) {
		boolean equal = true;
		for(int counter = 0; counter< one.getWidth(); counter++) {
			for(int secondCounter = 0; secondCounter< one.getHeight(); secondCounter++ ) {
				Pixel PixelForPictures1 = new Pixel(one,counter,secondCounter);
				Pixel PixelForPictures2 = new Pixel(two,counter,secondCounter);
				if (!pixelEqual(PixelForPictures1,PixelForPictures2))
					return false;

			}
		}
		return true;

	}
	/*if (other == this) return true;
	        if (other == null) return false;
	        if (other.getClass() != this.getClass()) return false;
	        Picture that = (Picture) other;
	        if (this.getWidth()!= that.getWidth())  return false;
	        if (this.getHeight() != that.getHeight()) return false;
	        for (int col = 0; col < getWidth(); col++)
	            for (int row = 0; row < getHeight(); row++)
	                if (this.getBasicPixel(col, row) != that.getBasicPixel(col, row)) return false;
	        return true;
	 */

	public int getBasicPixel(int x, int y)
	{
		return bufferedImage.getRGB(x,y);
	}
	public int getHeight() { return bufferedImage.getHeight(); }
	public int getWidth() { return bufferedImage.getWidth(); }
	

	private void checkMatch() {
		
		Picture selectedPicture = pictureForSquare(revealedLocations.get(0));
		/*		System.out.println(pic1);
		System.out.println(picture1());
		System.out.println();*/
		//Picture pic2 = pictureForSquare(revealedLocations.get(1));
		if (equals(selectedPicture, firstPuzzlePiece1()) || Picture1 ) {
			if(!(Picture1)){
				Picture1 = true;
				System.out.println("Yeet");
				System.out.println(Picture1);

				for (Pixel loc : revealedLocations) {
					statusGrid[loc.getX()][loc.getY()] = PERMANENTLY_REVEALED;
					permanentlyRevealedCount++;
				}

				revealedLocations.clear();
			}
		}
		
		System.out.println("equals(pic1, picture2()) && Picture1: " + equals(selectedPicture, secondPuzzlePiece()));
		if (equals(selectedPicture, secondPuzzlePiece()) && Picture1) {
			
			System.out.println("I'm getting there....not really");
			Picture2 = true;
			System.out.println("Yeet2");
			for (Pixel loc : revealedLocations) {
				statusGrid[loc.getX()][loc.getY()] = PERMANENTLY_REVEALED;
				permanentlyRevealedCount++;
			}

			revealedLocations.clear();

		}
		if (equals(selectedPicture, thirdPuzzlePiece3()) && Picture2) {
			System.out.println("Yeet");
			for (Pixel loc : revealedLocations) {
				statusGrid[loc.getX()][loc.getY()] = PERMANENTLY_REVEALED;
				permanentlyRevealedCount++;
			}
			revealedLocations.clear();
			Picture3 = true;
		}
		if (equals(selectedPicture, fourthPuzzlePiece4()) && Picture3) {
			System.out.println("Yeet4");
			for (Pixel loc : revealedLocations) {
				statusGrid[loc.getX()][loc.getY()] = PERMANENTLY_REVEALED;
				permanentlyRevealedCount++;
			}
			revealedLocations.clear();
			Picture4 = true;
		}
		if (equals(selectedPicture, fifthPuzzlePiece5()) && Picture4) {
			System.out.println("Yeet5");
			for (Pixel loc : revealedLocations) {
				statusGrid[loc.getX()][loc.getY()] = PERMANENTLY_REVEALED;
				permanentlyRevealedCount++;
			}
			revealedLocations.clear();
			Picture5 = true;
		}
		if (equals(selectedPicture, sixthPuzzlePiece6()) && Picture5) {
			System.out.println("Yeet6");
			for (Pixel loc : revealedLocations) {
				statusGrid[loc.getX()][loc.getY()] = PERMANENTLY_REVEALED;
				permanentlyRevealedCount++;
			}
			revealedLocations.clear();
			Picture6 = true;
		}
		if (equals(selectedPicture, seventhPuzzlePiece7()) && Picture6) {
			System.out.println("Yeet7");
			for (Pixel loc : revealedLocations) {
				statusGrid[loc.getX()][loc.getY()] = PERMANENTLY_REVEALED;
				permanentlyRevealedCount++;
			}
			revealedLocations.clear();
			Picture7 = true;
		}
		if (equals(selectedPicture, eigthPuzzlePiece8()) && Picture7) {
			System.out.println("Yeet8");
			for (Pixel loc : revealedLocations) {
				statusGrid[loc.getX()][loc.getY()] = PERMANENTLY_REVEALED;
				permanentlyRevealedCount++;
			}
			revealedLocations.clear();



		}
	}







	@Override
	public void mouseClickedAction(DigitalPicture pict, Pixel pix) {

		//System.out.println("Game being called");	
		Pixel selSquare = clickedSquare(pix);
		if (statusGrid[selSquare.getX()][selSquare.getY()] == COVERED) {
			revealedLocations.add(selSquare);
			statusGrid[selSquare.getX()][selSquare.getY()] = REVEALED;
			turnsCounter++;
			if (revealedLocations.size() == 1) {
				checkMatch();
			}else if (revealedLocations.size() > 1) {
				for (int i = 0; i < 2; i++) {
					Pixel loc = revealedLocations.remove(0);
					statusGrid[loc.getX()][loc.getY()] = COVERED;
				}
			}
			if (permanentlyRevealedCount >= xTiles * yTiles) {
				endGame();
			}
		}
		updateImage();
	}

	private void updateImage() {
		Picture disp = new Picture(imgHeight, imgWidth);
		Graphics2D graphics = disp.createGraphics();
		for (int x = 0; x < xTiles; x++) {
			for (int y = 0; y < yTiles; y++) {
				Picture pict;
				if (statusGrid[x][y] == COVERED) {
					pict = coverPicture;
				} else {
					pict = pictureGrid[x][y];
				}
				// This is used instead of the copy() method for performance
				// reasons
				graphics.drawImage(pict.getBufferedImage(), tileWidth * x,
						tileHeight * y, this);
			}
		}
		setImage(disp);
		// setImage() changes the title each time it's called
		setTitle("Puzzle");
	}

	private void endGame() {
		makePopUp("Congratulations! You won after " + (turnsCounter-8) + " turns");
		PictureExplorer windowAfterWinning = new PictureExplorer(coverPic);
	}

	@Override
	public boolean imageUpdate(Image arg0, int arg1, int arg2, int arg3,
			int arg4, int arg5) {
		return true;
	}

	// NOTE: This is mostly copied from the SimplePicture class's scale()
	// method, with one bugfix. If the bug is ever fixed in the SimplePicture
	// class, the scale() method can be safely used
	public static Picture pictureScale(Picture input, double xFactor,
			double yFactor) {
		// set up the scale transform
		AffineTransform scaleTransform = new AffineTransform();
		scaleTransform.scale(xFactor, yFactor);

		// create a new picture object that is the right size
		// BUGFIX (Tim Woodford 2/35/14): correct order of parameters
		Picture result = new Picture((int) (input.getHeight() * yFactor),
				(int) (input.getWidth() * xFactor));

		// get the graphics 2d object to draw on the result
		Graphics graphics = result.getGraphics();
		Graphics2D g2 = (Graphics2D) graphics;

		// draw the current image onto the result image scaled
		g2.drawImage(input.getImage(), scaleTransform, null);

		return result;
	}
	// You can modify this to include your own pictures
	private static BufferedImage cropImage(BufferedImage src, Rectangle rect, int x, int y) {//changed to static bc of error and I got annoyed
		/*System.out.println("Height: " + src.getHeight());
		System.out.println("Width: " + src.getWidth());
		System.out.println("x + width: " + (x+rect.width));
		System.out.println("y+height: " + (y+rect.height));*/
		BufferedImage dest = src.getSubimage(x, y, rect.width, rect.height);
		return dest; 
	}
	private  static Picture firstPuzzlePiece1() throws NullPointerException{
		//System.out.println("1");
		Image image = null;
		try {
			image = ImageIO.read(new File("Pictures/GameBackground.jpg"));
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		BufferedImage buffered = (BufferedImage) image;
		Rectangle bounds = new Rectangle(480, 540,480,540);
		if(buffered!= null) {
			BufferedImage FIRSTbuffered1  = cropImage(buffered,bounds,0,0);
			Picture  finalPic1 = new Picture(FIRSTbuffered1);

			return finalPic1;
		}
		throw new NullPointerException();
	}
	private  static Picture secondPuzzlePiece() {
		//System.out.println("2");
		Image image = null;
		try {
			image = ImageIO.read(new File("Pictures/GameBackground.jpg"));
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		BufferedImage buffered = (BufferedImage) image;
		Rectangle bounds = new Rectangle(480, 540,480,540);
		if(buffered!= null) {
			BufferedImage FIRSTbuffered1  = cropImage(buffered,bounds,480,0);
			Picture  finalPic1 = new Picture(FIRSTbuffered1);
			return finalPic1;

		}
		return null;
	}
	private  static Picture thirdPuzzlePiece3() {
		//System.out.println("3");
		Image image = null;
		try {
			image = ImageIO.read(new File("Pictures/GameBackground.jpg"));
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		BufferedImage buffered = (BufferedImage) image;
		Rectangle bounds = new Rectangle(480, 540,480,540);
		if(buffered!= null) {
			BufferedImage FIRSTbuffered1  = cropImage(buffered,bounds,960,0);
			Picture  finalPic1 = new Picture(FIRSTbuffered1);
			return finalPic1;

		}
		return null;
	}

	private  static Picture fourthPuzzlePiece4() {
		//	System.out.println("4");
		Image image = null;
		try {
			image = ImageIO.read(new File("Pictures/GameBackground.jpg"));
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		BufferedImage buffered = (BufferedImage) image;
		Rectangle bounds = new Rectangle(480, 540,480,540);
		if(buffered!= null) {
			BufferedImage FIRSTbuffered1  = cropImage(buffered,bounds,1440,0);
			Picture  finalPic1 = new Picture(FIRSTbuffered1);
			return finalPic1;

		}
		return null;
	}
	private  static Picture fifthPuzzlePiece5() {
		//	System.out.println("5");
		Image image = null;
		try {
			image = ImageIO.read(new File("Pictures/GameBackground.jpg"));
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		BufferedImage buffered = (BufferedImage) image;
		Rectangle bounds = new Rectangle(480, 540,480,540);
		if(buffered!= null) {
			BufferedImage FIRSTbuffered1  = cropImage(buffered,bounds,0,540);
			Picture  finalPic1 = new Picture(FIRSTbuffered1);
			return finalPic1;

		}
		return null;
	}
	private  static Picture sixthPuzzlePiece6() {
		//			System.out.println("6");
		Image image = null;
		try {
			image = ImageIO.read(new File("Pictures/GameBackground.jpg"));
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		BufferedImage buffered = (BufferedImage) image;
		Rectangle bounds = new Rectangle(480, 540,480,540);
		if(buffered!= null) {
			BufferedImage FIRSTbuffered1  = cropImage(buffered,bounds,480,540);
			Picture  finalPic1 = new Picture(FIRSTbuffered1);
			return finalPic1;

		}
		return null;
	}
	private  static Picture seventhPuzzlePiece7() {
		//	System.out.println("7");
		Image image = null;
		try {
			image = ImageIO.read(new File("Pictures/GameBackground.jpg"));
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		BufferedImage buffered = (BufferedImage) image;
		Rectangle bounds = new Rectangle(480, 540,480,540);
		if(buffered!= null) {
			BufferedImage FIRSTbuffered1  = cropImage(buffered,bounds,960,540);
			Picture  finalPic1 = new Picture(FIRSTbuffered1);
			return finalPic1;

		}
		return null;
	}
	private  static Picture eigthPuzzlePiece8() {
		//		System.out.println("8");
		Image image = null;
		try {
			image = ImageIO.read(new File("Pictures/GameBackground.jpg"));
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		BufferedImage buffered = (BufferedImage) image;
		Rectangle bounds = new Rectangle(480, 540,480,540);
		if(buffered!= null) {
			BufferedImage FIRSTbuffered1  = cropImage(buffered,bounds,1440,540);
			Picture  finalPic1 = new Picture(FIRSTbuffered1);
			return finalPic1;

		}
		return null;
	}

	public static void main(String[] args) {
		String basePath = "Pictures\\"; 
		//Picture coverPic = new Picture(basePath + "jokercard");

		Picture coverPic = new Picture(basePath+"GameBackground.jpg");
		new Game(pictures, coverPic, 1920, 1080, 4, 2);
		
	}

}

