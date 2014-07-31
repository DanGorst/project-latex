/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */

package project.latex.balloon.writer;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import org.junit.After;
import org.junit.AfterClass;
import static org.junit.Assert.*;
import org.junit.Before;
import org.junit.BeforeClass;
import org.junit.Test;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;
import project.latex.writer.DataWriteFailedException;

/**
 *
 * @author dgorst
 */
public class CameraFileWriterTest {
    
    private File mockImagesDirectory;
    private CameraFileWriter writer;
    
    public CameraFileWriterTest() {
    }
    
    @BeforeClass
    public static void setUpClass() {
    }
    
    @AfterClass
    public static void tearDownClass() {
    }
    
    @Before
    public void setUp() {
        this.mockImagesDirectory = mock(File.class);
    }
    
    void delete(File f) throws IOException {
        if (f.isDirectory()) {
            for (File c : f.listFiles()) {
                delete(c);
            }
        }
        if (!f.delete()) {
            throw new FileNotFoundException("Failed to delete file: " + f);
        }
    }
    
    @After
    public void tearDown() throws IOException {
        this.mockImagesDirectory = null;
        if (this.writer != null) {
            File imagesFolder = this.writer.getSavedImagesDirectory();
            if (imagesFolder.exists()) {
                delete(imagesFolder);
            }
            this.writer = null;
        }
    }
    
    @Test(expected = IllegalArgumentException.class)
    public void testCreatingWriterThrowsIfGivenNullImagesDir()  {
        new CameraFileWriter(null);
    }
    
    @Test(expected = IllegalArgumentException.class)
    public void testCreatingWriterThrowsIfGivenFileRatherThanDirectory()  {
        when(this.mockImagesDirectory.isDirectory()).thenReturn(false);
        new CameraFileWriter(this.mockImagesDirectory);
    }
    
    @Test
    public void testCreatingWriterCreatesDirectoryForSavedImages()  {
        File baseDirectory = new File(System.getProperty("user.dir"));
        writer = new CameraFileWriter(baseDirectory);
        File savedImagesDirectory = writer.getSavedImagesDirectory();
        assertTrue(savedImagesDirectory.exists());
        assertEquals(CameraFileWriter.imagesDirectoryName, savedImagesDirectory.getName());
    }
    
    @Test(expected = IllegalArgumentException.class)
    public void testWriterThrowsIfGivenNullDataObject() throws DataWriteFailedException {
        File baseDirectory = new File(System.getProperty("user.dir"));
        writer = new CameraFileWriter(baseDirectory);
        writer.writeImageFiles(null);
    }
    
    @Test
    public void testWriterDoesNothingIfGivenDataObjectWithNoImages() throws DataWriteFailedException {
        File baseDirectory = new File(System.getProperty("user.dir"));
        writer = new CameraFileWriter(baseDirectory);
        writer.writeImageFiles(new ArrayList<String>());
        File[] filesInImagesDir = writer.getSavedImagesDirectory().listFiles();
        assertEquals(0, filesInImagesDir.length);
    }
    
    @Test
    public void testWriterMovesFileIntoSavedImagesDirectory()   {
        try {
            File baseDirectory = new File(System.getProperty("user.dir"));
            writer = new CameraFileWriter(baseDirectory);
            
            File emptyImage = new File("test.jpg");
            emptyImage.createNewFile();
            List<String> imagePaths = new ArrayList<>();
            imagePaths.add(emptyImage.getPath());
            
            writer.writeImageFiles(imagePaths);
            
            File expectedFile = new File(writer.getSavedImagesDirectory().getPath() + File.separator + "test.jpg");
            assertTrue(expectedFile.exists());
            assertFalse(emptyImage.exists());
        } catch (IOException | DataWriteFailedException ex) {
            fail(ex.getMessage());
        }
    }
}
