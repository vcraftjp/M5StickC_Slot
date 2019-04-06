//
// dump_image.java
//

import java.awt.image.BufferedImage;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;

import javax.imageio.ImageIO;

public class dump_image {
	static final String CRLF = "\r\n";
	static final int MAX_COLUMN = 16;
	boolean isTranparent = false;
	int transparentColor = 0xFFFF; // white (not used)
	int width;
	int height;
	boolean isUnionSize = true;

	String srcFolder = "C:/Users/c.mos/Photo/M5Stack/slot/";
	String dstFolder = "C:/Users/c.mos/Dev/IoT/m5stack/projects/stick_slot/src/images/";

	String[] imageNames = { "seven", "bar", "logo", "cherry", "orange", "lemon" }; 

	dump_image() throws IOException {
		String dstPrefix = "slot_";
		StringBuilder sb = new StringBuilder();
		StringBuilder sbArray = new StringBuilder();

		for (String name : imageNames) {
			System.out.println(name + ".png");
			String srcPath = srcFolder + "slot_" + name + ".png";
			String dstPath = dstFolder + dstPrefix + name + ".h";
			String text = dumpImage(srcPath, null);
			
			BufferedWriter bw = new BufferedWriter(new FileWriter(new File(dstPath)));
			bw.write(text);
			bw.close();
			
			sb.append("#include \"" + dstPrefix + name + ".h\"" + CRLF);
			sbArray.append("\t" + dstPrefix + name + "," + CRLF);
		}
		if (isUnionSize) {
			sb.append(CRLF);
			sb.append("#define SYM_WIDTH " + width + CRLF);
			sb.append("#define SYM_HEIGHT " + height + CRLF);
			sb.append("#define SYM_COUNT " + imageNames.length + CRLF);
		}
		sb.append(CRLF);
		if (isTranparent) {
			sb.append("#define COLOR_TRANSP " + String.format("0x%04X", transparentColor) + CRLF);
			sb.append(CRLF);
		}
		sb.append("const uint16_t *slot_symbols[] = {" + CRLF);
		sb.append(sbArray);
		sb.append("};" + CRLF);

		BufferedWriter bw = new BufferedWriter(new FileWriter(new File(dstFolder + "slot_symbols.h")));
		bw.write(sb.toString());
		bw.close();
		System.out.println("done.");
	}
	
	String dumpImage(String path, String varName) throws IOException {
		File file = new File(path);
		BufferedImage bi = ImageIO.read(file);
		width = bi.getWidth();
		height = bi.getHeight();
		boolean hasAlpha = bi.getType() == BufferedImage.TYPE_4BYTE_ABGR;
		if (varName == null) {
			varName = file.getName();
			varName = varName.substring(0, varName.lastIndexOf('.')).toLowerCase();
			varName = varName.replaceAll("[\\s-]", "_");
		}
		StringBuilder sb = new StringBuilder();
		sb.append("#include <Arduino.h>" + CRLF);
		sb.append(CRLF);
		if (!isUnionSize) {
			sb.append("#define " + varName.toUpperCase() + "_WIDTH " + width + CRLF);
			sb.append("#define " + varName.toUpperCase() + "_HEIGHT " + height + CRLF);
			sb.append(CRLF);
		}
		sb.append("const uint16_t PROGMEM " + varName + "[] = {" + CRLF);
		int col = 0;
		for (int y = 0; y < height; y++) {
			for (int x = 0; x < width; x++) {
				if (col == 0) {
					sb.append("\t"); // indent
				}
				int rgb = bi.getRGB(x, y);
				int color16;
				if (hasAlpha && isTranparent && ((rgb & 0xFF000000) == 0)) {
					color16 = transparentColor;
				} else {
					color16 = color32To16(rgb);
				}				
				sb.append(String.format("0x%04X,", color16));
				if (++col >= MAX_COLUMN) {
					col = 0;
					sb.append(CRLF);
				}
			}
		}
		if (col != 0) {
			sb.append(CRLF);
		}
		sb.append("};" + CRLF);
		
		return sb.toString();
	}

	int color32To16(int rgb) {
		return color565((rgb >> 16) & 0xFF, (rgb >> 8) & 0xFF, rgb & 0xFF);
	}
	
	int color565(int r, int g, int b) {
		return ((r & 0xF8) << 8) | ((g & 0xFC) << 3) | (b >> 3);
	}
	
	public static void main(String[] args) throws IOException {
		new dump_image();
	
/*		if (args.length == 0) {
			System.out.println("usage: java -jar dump_image.jar <image file path> [<var name>]");
		} else {
			String path = args[0];
			String varName =  (args.length >= 2) ? args[1] : null;
			System.out.print(dumpImage(path, varName));
		}
*/
	}
}
