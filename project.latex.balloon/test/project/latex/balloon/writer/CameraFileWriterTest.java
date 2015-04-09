/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */

package project.latex.balloon.writer;

import java.io.File;
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
import project.latex.balloon.DataFolderResource;
import project.latex.balloon.TestFileDeleteHelper;

/**
 *
 * @author dgorst
 */
public class CameraFileWriterTest {
    
    private DataFolderResource mockDataFolderResource;
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
        this.mockDataFolderResource = mock(DataFolderResource.class);
    }
    
    @After
    public void tearDown() throws IOException {
        if (this.writer != null) {
            File imagesFolder = this.writer.getSavedImagesDirectory();
            if (imagesFolder.exists()) {
                TestFileDeleteHelper.delete(imagesFolder);
            }
            this.writer = null;
        }
    }
    
    @Test(expected = IllegalArgumentException.class)
    public void testCreatingWriterThrowsIfGivenNullImagesDir()  {
        new CameraFileWriter(null);
    }
    
    @Test
    public void testCreatingWriterCreatesDirectoryForSavedImages()  {
        File baseDirectory = new File(System.getProperty("user.dir"));
        when(mockDataFolderResource.getDataFolder()).thenReturn(baseDirectory);
        writer = new CameraFileWriter(mockDataFolderResource);
        File savedImagesDirectory = writer.getSavedImagesDirectory();
        assertTrue(savedImagesDirectory.exists());
        assertEquals(CameraFileWriter.imagesDirectoryName, savedImagesDirectory.getName());
    }
    
    @Test(expected = IllegalArgumentException.class)
    public void testWriterThrowsIfGivenNullDataObject() throws DataWriteFailedException {
        File baseDirectory = new File(System.getProperty("user.dir"));
        when(mockDataFolderResource.getDataFolder()).thenReturn(baseDirectory);
        writer = new CameraFileWriter(mockDataFolderResource);
        writer.writeImageFiles(null);
    }
    
    @Test
    public void testWriterDoesNothingIfGivenDataObjectWithNoImages() throws DataWriteFailedException {
        File baseDirectory = new File(System.getProperty("user.dir"));
        when(mockDataFolderResource.getDataFolder()).thenReturn(baseDirectory);
        writer = new CameraFileWriter(mockDataFolderResource);
        writer.writeImageFiles(new ArrayList<String>());
        File[] filesInImagesDir = writer.getSavedImagesDirectory().listFiles();
        assertEquals(0, filesInImagesDir.length);
    }
    
    @Test
    public void testWriterMovesFileIntoSavedImagesDirectory()   {
        try {
            File baseDirectory = new File(System.getProperty("user.dir"));
            when(mockDataFolderResource.getDataFolder()).thenReturn(baseDirectory);
            writer = new CameraFileWriter(mockDataFolderResource);
            
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
