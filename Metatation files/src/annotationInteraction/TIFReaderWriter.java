package annotationInteraction;

import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.Iterator;

import javax.imageio.IIOImage;
import javax.imageio.ImageIO;
import javax.imageio.ImageTypeSpecifier;
import javax.imageio.ImageWriter;
import javax.imageio.metadata.IIOInvalidTreeException;
import javax.imageio.metadata.IIOMetadata;
import javax.imageio.stream.FileImageOutputStream;

import com.sun.media.imageio.plugins.tiff.BaselineTIFFTagSet;
import com.sun.media.imageio.plugins.tiff.TIFFDirectory;
import com.sun.media.imageio.plugins.tiff.TIFFField;
import com.sun.media.imageio.plugins.tiff.TIFFTag;

public class TIFReaderWriter {

	private static int DPI = 600;
	
	private String file_path, file_name;
	private BufferedImage tif_file;
	
	public TIFReaderWriter(String file_path, String file_name){
		
		this.file_path = file_path;
		this.file_name = file_name + ".tif";

	}
	
	public BufferedImage readTIF(){
		
		tif_file = null;
		
		try {
			
			tif_file = ImageIO.read(new File(file_path + file_name));
			
		} catch (IOException e) {
			
			System.out.println("Exception when reading tif file \n");
			e.printStackTrace();
			
		}
		
		return tif_file;
		
	}
	
	public void copyTIFTo(String target_file_path, String target_file_name){
		
		Path source_path = Paths.get(file_path + file_name);
		Path target_path = Paths.get(target_file_path + target_file_name);
		
		try {
			
			Files.copy(source_path, target_path);
			
		} catch (IOException e) {

			System.out.println("Exception when copying TIF file \n");
			e.printStackTrace();

		}
		
	}
	
	public void writeTIF(BufferedImage tif_to_write){
		
		tif_file = tif_to_write;
		
		Iterator<ImageWriter> writers = ImageIO.getImageWritersByFormatName("TIFF");
		
		if(writers == null || !writers.hasNext()){
			
			System.out.println("No tif writer available \n");
			
		}
		
		ImageWriter writer = (ImageWriter) writers.next();
		
		ImageTypeSpecifier image_type = ImageTypeSpecifier.createFromRenderedImage(tif_file);
		IIOMetadata tif_meta_data = writer.getDefaultImageMetadata(image_type, null);
		
		try {
			
			tif_meta_data = set_TIF_meta_data(tif_meta_data);
			writer.setOutput(new FileImageOutputStream(new File(file_path + file_name)));
			writer.write(new IIOImage(tif_file, null, tif_meta_data));
			
		} catch (IOException e) {
			
			System.out.println("Exception when writing TIF file \n");
			e.printStackTrace();
			
		}
		
	}

	private IIOMetadata set_TIF_meta_data(IIOMetadata tif_meta_data) throws IIOInvalidTreeException{
		
		TIFFDirectory tif_dir = TIFFDirectory.createFromMetadata(tif_meta_data);

		BaselineTIFFTagSet base = BaselineTIFFTagSet.getInstance();
		TIFFTag tag_XRes = base.getTag(BaselineTIFFTagSet.TAG_X_RESOLUTION);
		TIFFTag tag_YRes = base.getTag(BaselineTIFFTagSet.TAG_Y_RESOLUTION);
		TIFFTag tag_ResUnit = base.getTag(BaselineTIFFTagSet.TAG_RESOLUTION_UNIT);
		
		TIFFField field_XRes = new TIFFField(tag_XRes, TIFFTag.TIFF_RATIONAL, 1, new long[][] {{DPI, 1}});
		TIFFField field_YRes = new TIFFField(tag_YRes, TIFFTag.TIFF_RATIONAL, 1, new long[][] {{DPI, 1}});
		TIFFField field_ResUnit = new TIFFField(tag_ResUnit, TIFFTag.TIFF_SHORT, 1, new char[] {BaselineTIFFTagSet.RESOLUTION_UNIT_INCH});
		
		tif_dir.addTIFFField(field_XRes);
		tif_dir.addTIFFField(field_YRes);
		tif_dir.addTIFFField(field_ResUnit);

		return tif_dir.getAsMetadata();
		
	}
	
	public boolean TIFExists(){
		
		return new File(file_path + file_name).exists();
		
	}
	
}
