package com.nrapoport.embeded.gpio.utilities;

import java.io.Serializable;
import java.util.HashMap;
import java.util.Map;

/**
 * <DL>
 * <DT>Description:</DT>
 * <DD>This class is used to map the cape pin designation (P0 - P47) to the bulldog library pin designation (as in
 * P8_11)</DD>
 * <DT>Date:</DT>
 * <DD>Apr 12, 2016</DD>
 * </DL>
 *
 * @author nrapo - Nick Rapoport
 *
 */
public class PinDescriptor implements Serializable {

    public static final String BULLDOG_PIN_FORMAT = ConfigSettings.getString("PinDescriptor.bulldog-pin-format");

    public static final String CAPE_PIN_ID = ConfigSettings.getString("PinDescriptor.cape-pin"); //$NON-NLS-1$

    public static final String HEADER = ConfigSettings.getString("PinDescriptor.header"); //$NON-NLS-1$

    public static final String HEADER_PIN = ConfigSettings.getString("PinDescriptor.header-pin"); //$NON-NLS-1$

    public static final String LEDSCAPE_PIN_FORMAT = ConfigSettings.getString("PinDescriptor.ledscape-pin-format");

    public static final String LEDSCAPE_PIN_ID = ConfigSettings.getString("PinDescriptor.ledscape-pin"); //$NON-NLS-1$

    private static final org.slf4j.Logger log = org.slf4j.LoggerFactory.getLogger(PinDescriptor.class);

    public static final String NAME = ConfigSettings.getString("PinDescriptor.name"); //$NON-NLS-1$

    private static QueryOutputFormat queryFormat;

    /**
     * <DL>
     * <DT>serialVersionUID</DT>
     * <DD>default serialVersionUID (1)</DD>
     * </DL>
     */
    private static final long serialVersionUID = 1L;

    /**
     * <DL>
     * <DT>Description:</DT>
     * <DD>The queryFormat header (will return the correct header for the queryFormat value</DD>
     * <DT>Date:</DT>
     * <DD>Apr 15, 2016</DD>
     * </DL>
     *
     * @return the value of queryFormat header field
     */
    public static String getHeaderFormat() {
        return ConfigSettings.getString("PinDescriptor.query-format-" + getQueryFormat().toString() + "-header"); //$NON-NLS-1$

    }

    /**
     * <DL>
     * <DT>Description:</DT>
     * <DD>Getter for the queryFormat property</DD>
     * <DT>Date:</DT>
     * <DD>Apr 15, 2016</DD>
     * </DL>
     *
     * @return the value of queryFormat field
     */
    public static QueryOutputFormat getQueryFormat() {
        if (queryFormat == null) {
            setQueryFormat(QueryOutputFormat.valueOf(ConfigSettings.getString("ValidateGPIO.query-format-default"))); //$NON-NLS-1$
        }
        return queryFormat;
    }

    /**
     * <DL>
     * <DT>Description:</DT>
     * <DD>The queryFormat row format (will return the correct row format for the queryFormat value</DD>
     * <DT>Date:</DT>
     * <DD>Apr 15, 2016</DD>
     * </DL>
     *
     * @return the value of queryFormat row format field
     */
    public static String getRowFormat() {
        return ConfigSettings.getString("PinDescriptor.query-format-" + getQueryFormat().toString()); //$NON-NLS-1$

    }

    /**
     * <DL>
     * <DT>Description:</DT>
     * <DD>The queryFormat multi-value separator (will return the correct separator for the queryFormat value</DD>
     * <DT>Date:</DT>
     * <DD>Apr 15, 2016</DD>
     * </DL>
     *
     * @return the value of queryFormat separator field
     */
    public static String getSeparatorFormat() {
        return ConfigSettings.getString("PinDescriptor.query-format-" + getQueryFormat().toString() + "-sep"); //$NON-NLS-1$

    }

    /**
     * <DL>
     * <DT>Description:</DT>
     * <DD>The queryFormat trailer (will return the correct trailer for the queryFormat value</DD>
     * <DT>Date:</DT>
     * <DD>Apr 15, 2016</DD>
     * </DL>
     *
     * @return the value of queryFormat trailer field
     */
    public static String getTrailerFormat() {
        return ConfigSettings.getString("PinDescriptor.query-format-" + getQueryFormat().toString() + "-trailer"); //$NON-NLS-1$

    }

    /**
     * <DL>
     * <DT>Description:</DT>
     * <DD>Setter for the queryFormat property</DD>
     * <DT>Date:</DT>
     * <DD>Apr 15, 2016</DD>
     * </DL>
     *
     * @param aQueryFormat
     *            new value for the queryFormat property
     */
    public static void setQueryFormat(final QueryOutputFormat aQueryFormat) {
        queryFormat = aQueryFormat;
    }

    private Map<String, String> descriptor;

    /**
     * <DL>
     * <DT>Description:</DT>
     * <DD>PinDescriptor Constructor</DD>
     * <DT>Date:</DT>
     * <DD>Apr 12, 2016</DD>
     * </DL>
     */
    public PinDescriptor() {
        super();
    }

    /**
     * <DL>
     * <DT>Description:</DT>
     * <DD>PinDescriptor Constructor from a map that contains keys & values for Header, HeaderPin, Name and CapePinId</DD>
     * <DT>Date:</DT>
     * <DD>Apr 12, 2016</DD>
     * </DL>
     *
     * @param inDescriptor
     */
    public PinDescriptor(final Map<String, String> inDescriptor) {
        this();
        setDescriptor(inDescriptor);
    }

    /**
     * <DL>
     * <DT>Description:</DT>
     * <DD>Bulldog library pin designation for this pin (header.pin) like P8_22</DD>
     * <DT>Date:</DT>
     * <DD>Apr 12, 2016</DD>
     * </DL>
     *
     * @return Bulldog library pin designation
     */
    String getBulldogHeaderPin() {
        return String.format(BULLDOG_PIN_FORMAT, getDescriptor().get(HEADER), getDescriptor().get(HEADER_PIN));
    }

    /**
     * <DL>
     * <DT>Description:</DT>
     * <DD>Cape pin identifier (P0 - P47)</DD>
     * <DT>Date:</DT>
     * <DD>Apr 12, 2016</DD>
     * </DL>
     *
     * @return Cape pin identifier
     */
    String getCapePin() {
        return getDescriptor().get(CAPE_PIN_ID);
    }

    /**
     * <DL>
     * <DT>Description:</DT>
     * <DD>Getter for the descriptor property</DD>
     * <DT>Date:</DT>
     * <DD>Apr 12, 2016</DD>
     * </DL>
     *
     * @return the value of descriptor field
     */
    public Map<String, String> getDescriptor() {
        if (descriptor == null) {
            setDescriptor(new HashMap<String, String>());
            descriptor.put(HEADER, "0"); //$NON-NLS-1$
            descriptor.put(HEADER_PIN, "0"); //$NON-NLS-1$
            descriptor.put(NAME, "GPIO0_0"); //$NON-NLS-1$
            descriptor.put(CAPE_PIN_ID, "0"); //$NON-NLS-1$
            descriptor.put(LEDSCAPE_PIN_ID, ""); //$NON-NLS-1$
        }
        return descriptor;
    }

    /**
     * <DL>
     * <DT>Description:</DT>
     * <DD>The header this pin is on (P8 or P9)</DD>
     * <DT>Date:</DT>
     * <DD>Apr 12, 2016</DD>
     * </DL>
     *
     * @return the header this pin is on
     */
    String getHeader() {
        return getDescriptor().get(HEADER);
    }

    /**
     * <DL>
     * <DT>Description:</DT>
     * <DD>The header this pin is on (P8 or P9)</DD>
     * <DT>Date:</DT>
     * <DD>Apr 12, 2016</DD>
     * </DL>
     *
     * @return the header this pin is on
     */
    String getHeaderPin() {
        return getDescriptor().get(HEADER_PIN);
    }

    /**
     * <DL>
     * <DT>Description:</DT>
     * <DD>Get the kernel pin (GPIO bank x 32 + pin number from name</DD>
     * <DT>Date:</DT>
     * <DD>Apr 12, 2016</DD>
     * </DL>
     *
     * @return the kernel pin
     */
    int getKernelPin() {
        final String name = getDescriptor().get(NAME);
        final String[] vars =
            name != null && name.indexOf("GPIO") > -1 ? name.substring(4).split("_") : new String[] { name, "" }; //$NON-NLS-1$ $NON-NLS-2$
        if (vars.length > 1) {
            log.trace("[1]={} [2]={}", vars[0], vars[1]); //$NON-NLS-1$
        }
        return name.indexOf("GPIO") > -1 ? Integer.parseInt(vars[0]) * 32 + Integer.parseInt(vars[1]) : 0; //$NON-NLS-1$

    }

    /**
     * <DL>
     * <DT>Description:</DT>
     * <DD>The LEDScape Mapping for this pin</DD>
     * <DT>Date:</DT>
     * <DD>Apr 12, 2016</DD>
     * </DL>
     *
     * @return the header this pin is on
     */
    String getLEDScapePin() {
        final String ledPin = String.format(LEDSCAPE_PIN_FORMAT, getDescriptor().get(LEDSCAPE_PIN_ID));
        return ledPin.equalsIgnoreCase("L") ? "" : ledPin;
    }

    /**
     * <DL>
     * <DT>Description:</DT>
     * <DD>BBB Name of this pin i.e GPIO2_7</DD>
     * <DT>Date:</DT>
     * <DD>Apr 12, 2016</DD>
     * </DL>
     *
     * @return BBB Name of the pin
     */
    String getName() {
        return getDescriptor().get(NAME);
    }

    /**
     * <DL>
     * <DT>Description:</DT>
     * <DD>Header pin number for this pin (1-46)</DD>
     * <DT>Date:</DT>
     * <DD>Apr 12, 2016</DD>
     * </DL>
     *
     * @return Header pin number
     */
    String getPin() {
        return getDescriptor().get(HEADER_PIN);
    }

    /**
     * <DL>
     * <DT>Description:</DT>
     * <DD>Setter for the descriptor property</DD>
     * <DT>Date:</DT>
     * <DD>Apr 12, 2016</DD>
     * </DL>
     *
     * @param aDescriptor
     *            new value for the descriptor property
     */
    public void setDescriptor(final Map<String, String> aDescriptor) {
        descriptor = aDescriptor;
    }

    /** {@inheritDoc} */
    @Override
    public String toString() {
        return String.format(getRowFormat(), getBulldogHeaderPin(), getHeader(), getPin(), getName(), getCapePin(),
            getKernelPin(), getLEDScapePin());
    }
}
