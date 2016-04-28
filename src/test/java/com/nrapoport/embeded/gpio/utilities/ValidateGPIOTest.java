/**
 * @author nrapo - Nick Rapoport
 * @copyright Copyright 2016 ( Apr 12, 2016 ) Nick Rapoport all rights reserved.
 */
package com.nrapoport.embeded.gpio.utilities;

import static org.junit.Assert.*;

import java.util.Arrays;
import java.util.List;

import org.junit.After;
import org.junit.AfterClass;
import org.junit.Before;
import org.junit.BeforeClass;
import org.junit.Test;

/**
 * <DL>
 * <DT>Description:</DT>
 * <DD>TODO add description</DD>
 * <DT>Date:</DT>
 * <DD>Apr 12, 2016</DD>
 * </DL>
 *
 * @author nrapo - Nick Rapoport
 *
 */
public class ValidateGPIOTest {
    private static org.slf4j.Logger log = org.slf4j.LoggerFactory.getLogger(ValidateGPIOTest.class);

    /**
     * <DL>
     * <DT>Description:</DT>
     * <DD>TODO add setUpBeforeClass description</DD>
     * <DT>Date:</DT>
     * <DD>Apr 12, 2016</DD>
     * </DL>
     *
     * @throws java.lang.Exception
     */
    @BeforeClass
    public static void setUpBeforeClass() throws Exception {
    }

    /**
     * <DL>
     * <DT>Description:</DT>
     * <DD>TODO add tearDownAfterClass description</DD>
     * <DT>Date:</DT>
     * <DD>Apr 12, 2016</DD>
     * </DL>
     *
     * @throws java.lang.Exception
     */
    @AfterClass
    public static void tearDownAfterClass() throws Exception {
    }

    /**
     * <DL>
     * <DT>Description:</DT>
     * <DD>TODO add setUp description</DD>
     * <DT>Date:</DT>
     * <DD>Apr 12, 2016</DD>
     * </DL>
     *
     * @throws java.lang.Exception
     */
    @Before
    public void setUp() throws Exception {
    }

    /**
     * <DL>
     * <DT>Description:</DT>
     * <DD>TODO add tearDown description</DD>
     * <DT>Date:</DT>
     * <DD>Apr 12, 2016</DD>
     * </DL>
     *
     * @throws java.lang.Exception
     */
    @After
    public void tearDown() throws Exception {
    }

    /**
     * Test method for {@link com.nrapoport.embeded.gpio.utilities.ValidateGPIO#ValidateGPIO(java.lang.String[])}.
     */
    @Test
    public final void test01ValidateGPIO() {
        final String[] args = new String[] { "-h" };
        final ValidateGPIO vGPIO = new ValidateGPIO(args);
        assertNotNull(vGPIO);
        assertEquals(CLIParseStatus.HELP, vGPIO.getStatus());

    }

    /**
     * Test method for {@link com.nrapoport.embeded.gpio.utilities.ValidateGPIO#ValidateGPIO(java.lang.String[])}.
     */
    @Test
    public final void test02ValidateGPIO() {
        final String[] args = new String[] { "-dout" };
        final ValidateGPIO vGPIO = new ValidateGPIO(args);
        assertNotNull(vGPIO);
        assertEquals(CLIParseStatus.SUCCESS, vGPIO.getStatus());
        assertEquals(ValidateGPIO.DIRECTION_OUT, vGPIO.getOptionValues().get(ValidateGPIO.DIRECTION_SHORT));
    }

    /**
     * Test method for {@link com.nrapoport.embeded.gpio.utilities.ValidateGPIO#ValidateGPIO(java.lang.String[])}.
     */
    @Test
    public final void test03ValidateGPIO() {

        final String[] args = new String[] { "-din" };
        final ValidateGPIO vGPIO = new ValidateGPIO(args);
        assertNotNull(vGPIO);
        assertEquals(CLIParseStatus.SUCCESS, vGPIO.getStatus());
        assertEquals(ValidateGPIO.DIRECTION_IN, vGPIO.getOptionValues().get(ValidateGPIO.DIRECTION_SHORT));

    }

    /**
     * Test method for {@link com.nrapoport.embeded.gpio.utilities.ValidateGPIO#ValidateGPIO(java.lang.String[])}.
     */
    @Test
    public final void test04ValidateGPIO() {
        final String[] args = new String[] { "-dout", "-pP0,P46,P8_22" };
        final ValidateGPIO vGPIO = new ValidateGPIO(args);
        assertNotNull(vGPIO);
        assertEquals(CLIParseStatus.SUCCESS, vGPIO.getStatus());
        assertEquals(ValidateGPIO.DIRECTION_OUT, vGPIO.getOptionValues().get(ValidateGPIO.DIRECTION_SHORT));
        final String[] expecteds = new String[] { "P8_32", "P9_21", "P8_22" };
        @SuppressWarnings("unchecked")
        final List<String> list = (List<String>) vGPIO.getOptionValues().get(ValidateGPIO.PIN_LIST_SHORT);
        assertArrayEquals(expecteds, list.toArray(new String[list.size()]));

    }

    /**
     * Test method for {@link com.nrapoport.embeded.gpio.utilities.ValidateGPIO#ValidateGPIO(java.lang.String[])}.
     */
    @Test
    public final void test05ValidateGPIO() {
        final String[] args = new String[] { "-dout", "-qall" };
        final ValidateGPIO vGPIO = new ValidateGPIO(args);
        assertNotNull(vGPIO);
        assertEquals(CLIParseStatus.SUCCESS, vGPIO.getStatus());
        assertEquals(ValidateGPIO.DIRECTION_OUT, vGPIO.getOptionValues().get(ValidateGPIO.DIRECTION_SHORT));
        final String[] expecteds =
            vGPIO.getPinReverseMappings().keySet().toArray(new String[vGPIO.getPinReverseMappings().keySet().size()]);
        @SuppressWarnings("unchecked")
        final List<String> list = (List<String>) vGPIO.getOptionValues().get(ValidateGPIO.QUERY_SHORT);
        assertArrayEquals(expecteds, list.toArray(new String[list.size()]));
    }

    /**
     * Test method for {@link com.nrapoport.embeded.gpio.utilities.ValidateGPIO#ValidateGPIO(java.lang.String[])}.
     */
    @Test
    public final void test06ValidateGPIO() {
        final String[] expecteds = new String[] { "P8_32", "P9_21", "P8_22" };
        final List<String> validQueryFromats = Arrays.asList(new String[] { "-fJSON", "-fCSV", "-fXML", "" });
        for (int i = 0; i < validQueryFromats.size(); i++) {
            log.debug("QUERY_FORMAT={}", validQueryFromats.get(i));
            final String[] args = new String[] { "-dout", "-qP0,P46,P8_22", validQueryFromats.get(i) };
            final ValidateGPIO vGPIO = new ValidateGPIO(args);
            assertNotNull(vGPIO);
            assertEquals(CLIParseStatus.SUCCESS, vGPIO.getStatus());
            assertEquals(ValidateGPIO.DIRECTION_OUT, vGPIO.getOptionValues().get(ValidateGPIO.DIRECTION_SHORT));
            @SuppressWarnings("unchecked")
            final List<String> list = (List<String>) vGPIO.getOptionValues().get(ValidateGPIO.QUERY_SHORT);
            assertArrayEquals(expecteds, list.toArray(new String[list.size()]));
        }
    }

}
