/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package project.latex.balloon;

import java.io.File;
import java.io.IOException;
import java.util.Calendar;
import org.junit.After;
import org.junit.Test;
import static org.junit.Assert.*;

/**
 *
 * @author dgorst
 */
public class DataFolderResourceTest {

    private DataFolderResource dataFolderResource;
    
    @After
    public void tearDown() throws IOException {
        if (this.dataFolderResource != null) {
            File dataFolder = dataFolderResource.getDataFolder();
            if (dataFolder.exists()) {
                TestFileDeleteHelper.delete(dataFolder);
            }
            this.dataFolderResource = null;
        }
    }
    
    /**
     * Test of getDataFolder method, of class DataFolderResource.
     * @throws java.io.IOException
     */
    @Test
    public void testGetDataFolderReturnsValidFolder() throws IOException {
        Calendar calendar = Calendar.getInstance();
        calendar.set(2015, 5, 10, 12, 0, 0);
        dataFolderResource = new DataFolderResource(calendar.getTime(), "data");
        File result = dataFolderResource.getDataFolder();
        assertNotNull(result);
        assertTrue(result.isDirectory());
        assertEquals("Flight starting - 10-06-2015 12-00-00", result.getName());
    }
    
}
