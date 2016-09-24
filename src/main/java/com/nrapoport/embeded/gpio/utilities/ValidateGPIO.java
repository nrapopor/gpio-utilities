package com.nrapoport.embeded.gpio.utilities;

import io.silverspoon.bulldog.core.Signal;
import io.silverspoon.bulldog.core.gpio.DigitalInput;
import io.silverspoon.bulldog.core.gpio.DigitalOutput;
import io.silverspoon.bulldog.core.pin.PinBlockedException;
import io.silverspoon.bulldog.core.platform.Board;
import io.silverspoon.bulldog.core.platform.Platform;
import io.silverspoon.bulldog.core.util.BulldogUtil;
import io.silverspoon.bulldog.devices.switches.Button;
import io.silverspoon.bulldog.devices.switches.ButtonListener;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.Reader;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

import org.apache.commons.cli.CommandLine;
import org.apache.commons.cli.CommandLineParser;
import org.apache.commons.cli.DefaultParser;
import org.apache.commons.cli.HelpFormatter;
import org.apache.commons.cli.Option;
import org.apache.commons.cli.OptionGroup;
import org.apache.commons.cli.Options;
import org.apache.commons.cli.ParseException;
import org.apache.commons.csv.CSVFormat;
import org.apache.commons.csv.CSVRecord;

/**
 * @author nrapopor
 *
 */
public class ValidateGPIO {

    public static final String LIST_SEPARATOR_REGEX = ConfigSettings.getString("ValidateGPIO.list-separator-regex"); //$NON-NLS-1$

    public static final String PARSE_ERROR_FMT = ConfigSettings.getString("ValidateGPIO.error-message-fmt"); //$NON-NLS-1$

    public static org.slf4j.Logger log = org.slf4j.LoggerFactory.getLogger(ValidateGPIO.class);

    public static final String ALL = "all"; //$NON-NLS-1$

    public static final String PIN_LIST = ConfigSettings.getString("ValidateGPIO.pins-long"); //$NON-NLS-1$

    public static final String PIN_LIST_NAME = ConfigSettings.getString("ValidateGPIO.pins-name"); //$NON-NLS-1$

    public static final String PIN_LIST_SHORT = ConfigSettings.getString("ValidateGPIO.pins-short"); //$NON-NLS-1$

    public static final String PIN_LIST_EXISTS = "is_" + PIN_LIST_SHORT; //$NON-NLS-1$

    public static final String PIN_LIST_DESC = ConfigSettings.getString("ValidateGPIO.pins-description"); //$NON-NLS-1$

    public static final String PIN_LIST_HEADER_REGEX = ConfigSettings.getString("ValidateGPIO.pins-header-regex"); //$NON-NLS-1$

    public static final String PIN_LIST_DEFAULT_STR = ConfigSettings.getString("ValidateGPIO.pins-default"); //$NON-NLS-1$

    public static final List<String> PIN_LIST_DEFAULT = Arrays.asList(PIN_LIST_DEFAULT_STR.split(LIST_SEPARATOR_REGEX));

    //    private static final Set<String> VALID_PINS =
    //        new HashSet<>(Arrays.asList(ConfigSettings.getString("ValidateGPIO.valid-pins").split("[ ]*,[ ]*"))); //$NON-NLS-1$

    public static final String CAPE_PIN_MAP = ConfigSettings.getString("ValidateGPIO.pins-cape-pin-map"); //$NON-NLS-1$

    public static final String DIRECTION = ConfigSettings.getString("ValidateGPIO.direction-long"); //$NON-NLS-1$

    //private static final String PIN_LIST_ID = "p"; //$NON-NLS-1$

    public static final String DIRECTION_SHORT = ConfigSettings.getString("ValidateGPIO.direction-short"); //$NON-NLS-1$

    public static final String DIRECTION_NAME = ConfigSettings.getString("ValidateGPIO.direction-name"); //$NON-NLS-1$

    public static final String DIRECTION_DESC = ConfigSettings.getString("ValidateGPIO.direction-description"); //$NON-NLS-1$

    public static final String DIRECTION_DEFAULT = ConfigSettings.getString("ValidateGPIO.direction-default"); //$NON-NLS-1$

    public static final String DIRECTION_OUT = "out"; //$NON-NLS-1$

    public static final String DIRECTION_IN = "in"; //$NON-NLS-1$

    public static final String DIRECTION_VALID_STR = ConfigSettings.getString("ValidateGPIO.direction-valid-list"); //$NON-NLS-1$

    public static final List<String> DIRECTION_VALID = Arrays.asList(DIRECTION_VALID_STR.split(LIST_SEPARATOR_REGEX));

    public static final String QUERY_FORMAT = ConfigSettings.getString("ValidateGPIO.query-format-long"); //$NON-NLS-1$

    //private static final String PIN_LIST_ID = "p"; //$NON-NLS-1$

    public static final String QUERY_FORMAT_SHORT = ConfigSettings.getString("ValidateGPIO.query-format-short"); //$NON-NLS-1$

    public static final String QUERY_FORMAT_NAME = ConfigSettings.getString("ValidateGPIO.query-format-name"); //$NON-NLS-1$

    public static final String QUERY_FORMAT_DESC = ConfigSettings.getString("ValidateGPIO.query-format-description"); //$NON-NLS-1$

    public static final String QUERY_FORMAT_DEFAULT = ConfigSettings.getString("ValidateGPIO.query-format-default"); //$NON-NLS-1$

    public static final String QUERY_FORMAT_VALID_STR = ConfigSettings
        .getString("ValidateGPIO.query-format-valid-list"); //$NON-NLS-1$

    public static final List<String> QUERY_FORMAT_VALID = Arrays.asList(QUERY_FORMAT_VALID_STR
        .split(LIST_SEPARATOR_REGEX));

    public static final String HELP = "help"; //$NON-NLS-1$

    public static final String HELP_ID = "h"; //$NON-NLS-1$

    public static final String QUERY = ConfigSettings.getString("ValidateGPIO.query-long"); //$NON-NLS-1$

    //private static final String PIN_LIST_ID = "p"; //$NON-NLS-1$

    public static final String QUERY_SHORT = ConfigSettings.getString("ValidateGPIO.query-short"); //$NON-NLS-1$

    public static final String QUERY_NAME = ConfigSettings.getString("ValidateGPIO.query-name"); //$NON-NLS-1$

    public static final String QUERY_DEFAULT = ConfigSettings.getString("ValidateGPIO.query-default"); //$NON-NLS-1$

    public static final String QUERY_DESC = ConfigSettings.getString("ValidateGPIO.query-description"); //$NON-NLS-1$

    public static void main(final String[] args) {
        final ValidateGPIO validate = new ValidateGPIO(args);
        validate.execute();
        //System.out.println("Hello World!"); //$NON-NLS-1$
    }

    private Options options;

    private CLIParseStatus status;

    private String parseErrorMessage;

    private Map<String, Object> optionValues = new HashMap<>();

    private Map<String, PinDescriptor> pinMappings = new HashMap<>();

    private Map<String, PinDescriptor> pinReverseMappings = new HashMap<>();

    private Map<String, PinDescriptor> pinLEDScapeMappings = new HashMap<>();

    /**
     * <DL>
     * <DT>Description:</DT>
     * <DD>ValidateGPIO Constructor</DD>
     * <DT>Date:</DT>
     * <DD>Apr 12, 2016</DD>
     * </DL>
     */
    public ValidateGPIO(final String[] args) {
        super();
        setStatus(CLIParseStatus.SUCCESS);
        setParseErrorMessage(""); //$NON-NLS-1$
        if (loadPinMappings()) {
            try {
                parseArguments(args);
            } catch (final Exception ex) {
                // TODO Auto-generated catch block
                log.error("caught Exception :", ex); //$NON-NLS-1$
                //System.err.println("caught Exception :"+ex.toString()); //$NON-NLS-1$
                //ex.printStackTrace(System.err);
                setStatus(CLIParseStatus.GENERAL_ERROR);
                setParseErrorMessage(String.format(PARSE_ERROR_FMT, ex.toString()));
            }
        } else {
            setStatus(CLIParseStatus.GENERAL_ERROR);
        }
    }

    protected Options configureOptions() {
        final Options argOptions = new Options();
        argOptions // add options
        .addOption( // add an option
            Option.builder(HELP_ID) // using option builder
            .argName(HELP) // name
            .longOpt(HELP) // long option name
            .hasArg(false) // has # arguments or not
            .desc("print this help message") //$NON-NLS-1$
            .required(false) // required or not
            .build()) // create
            .addOption( // add an option
                Option.builder(DIRECTION_SHORT) // using option builder
                .argName(DIRECTION_NAME) // name
                .longOpt(DIRECTION) // long option name
                .hasArg(true) // has # arguments or not
                //.numberOfArgs(48) // number of arguments
                //.type(Integer.class) // the value type
                .desc(formatDescription(DIRECTION_DESC, DIRECTION_DEFAULT, DIRECTION_VALID_STR)) // description
                .required(false) // required or not
                .build()) // create
                .addOption( // add an option
                    Option.builder(QUERY_FORMAT_SHORT) // using option builder
                    .argName(QUERY_FORMAT_NAME) // name
                    .longOpt(QUERY_FORMAT) // long option name
                    .hasArg(true) // has # arguments or not
                    //.numberOfArgs(48) // number of arguments
                    //.type(Integer.class) // the value type
                    .desc(formatDescription(QUERY_FORMAT_DESC, QUERY_FORMAT_DEFAULT, QUERY_FORMAT_VALID_STR)) // description
                    .required(false) // required or not
                    .build()); // create
        final OptionGroup optGroup = new OptionGroup().addOption( // add an option
            Option.builder(PIN_LIST_SHORT) // using option builder
                .argName(PIN_LIST_NAME) // name
                .longOpt(PIN_LIST) // long option name
                .hasArgs() // has # arguments or not
                .numberOfArgs(46) // number of arguments
                .valueSeparator(',') //values separator
                //.type(Integer.class) // the value type
                .desc(formatDescription(PIN_LIST_DESC, PIN_LIST_DEFAULT_STR, null)) // description
                .required(false) // required or not
                .build()) // create
            .addOption( // add an option
                Option.builder(QUERY_SHORT) // using option builder
                .argName(QUERY_NAME) // name
                .longOpt(QUERY) // long option name
                .hasArgs() // has # arguments or not
                .numberOfArgs(46) // number of arguments
                .valueSeparator(',') //values separator
                //.type(Integer.class) // the value type
                .desc(formatDescription(QUERY_DESC, QUERY_DEFAULT, null)) // description
                .required(false) // required or not
                .build()); // create
        optGroup.setRequired(false);
        argOptions.addOptionGroup(optGroup);
        //      .addOption( // add an option
        //	    Option.builder() // using option builder
        //          .argName(BEAN_NAME_SHORT) // name
        //          .longOpt(BEAN_NAME) // long option name
        //          .hasArg() // has # arguments or not
        //          .numberOfArgs(1) // has # arguments or not
        //          .desc("Alternate IManagedBean Spring bean name defaults to " + //$NON-NLS-1$
        //            "the simple class name of the implementing class ") //$NON-NLS-1$
        //          .required(false) // required or not
        //          .build()) // create
        //         //.create(ManagedBeanCLI.BEAN_NAME_ID)) // create
        //      .addOption( // add an option
        //    	Option.builder() // using option builder
        //          .argName(CONFIG_FILE_SHORT) // name
        //          .longOpt(CONFIG_FILE) // long name
        //          .hasArg() // has  arguments
        //          .numberOfArgs(1) // has # arguments or not
        //          .desc("Alternate Spring Configuration file") //$NON-NLS-1$
        //          .build()); // create
        //.create(CONFIG_FILE_ID)); // create
        // OptionGroup
        // optGroup =
        // new
        // OptionGroup().addOption(
        // OptionBuilder.withArgName(USER_NAME).withLongOpt(USER_NAME)
        // .hasArg().withDescription("user name to create/update")
        // .create(USER_NAME_ID)).addOption(
        // OptionBuilder.withArgName(CONFIG_FILE).withLongOpt(CONFIG_FILE)
        // .hasArg().withDescription(
        // "Security Configuration file based on "
        // +
        // "ConfigureObjects.xsd").create(CONFIG_FILE_ID))
        // .addOption(
        // OptionBuilder.withArgName(LIST_AUTH).withLongOpt(LIST_AUTH)
        // .hasArg(false).withDescription(
        // "Lists the default authentication domain").create(
        // LIST_AUTH_ID)).addOption(
        // OptionBuilder.withArgName(LIST_POLICY).withLongOpt(
        // LIST_POLICY).hasArg(false).withDescription(
        // "Lists the default policies").create(LIST_POLICY_ID));
        // optGroup.setRequired(true);
        // argOptions.addOptionGroup(optGroup);

        return argOptions;
    }

    /**
     * <DL>
     * <DT>Description:</DT>
     * <DD>validate pins;</DD>
     * <DT>Date:</DT>
     * <DD>Apr 12, 2016</DD>
     * </DL>
     */
    private void execute() {
        if (getStatus() == CLIParseStatus.SUCCESS && (Boolean) getOptionValues().get(PIN_LIST_EXISTS)) {
            @SuppressWarnings("unchecked")
            final List<String> pins = (List<String>) getOptionValues().get(PIN_LIST_SHORT);
            final String direction = getOptionValues().get(DIRECTION_SHORT).toString();
            final Board board = Platform.createBoard();

            if (direction.equalsIgnoreCase(DIRECTION_OUT)) {
                for (final String pinName : pins) {
                    //Set up a digital output

                    try {
                        final DigitalOutput output = board.getPin(pinName).as(DigitalOutput.class);
                        for (int i = 0; i < 10; i++) {
                            output.high();
                            BulldogUtil.sleepMs(500);
                            output.low();
                            BulldogUtil.sleepMs(500);
                        }
                    } catch (final PinBlockedException ex) {
                        log.warn("Pin {}/{} is blocked : {} ", pinName, getPinReverseMappings().get(pinName)
                            .getCapePin(), ex.getMessage());
                    }
                }
            } else if (direction.equalsIgnoreCase(DIRECTION_IN)) {
                final List<Button> allButtons = new ArrayList<>(pins.size());
                for (final String pinName : pins) {
                    //Set up a digital output
                    //Set up a digital input
                    final DigitalInput buttonSignal = board.getPin(pinName).as(DigitalInput.class);

                    //Create the button with this DigitalInput
                    final Button button = new Button(buttonSignal, Signal.Low);

                    //Add a button listener
                    button.addListener(new ButtonListener() {

                        @Override
                        public void buttonPressed() {
                            log.info("{} pin is PRESSED", pinName);
                            //System.out.println("PRESSED");
                        }

                        @Override
                        public void buttonReleased() {
                            log.info("{} pin is RELEASED", pinName);
                            //System.out.println("RELEASED");
                        }

                    });
                    allButtons.add(button);
                }
                while (true) {
                    BulldogUtil.sleepMs(50);
                }

            }
        } else if (getStatus() != CLIParseStatus.HELP) {
            log.error(getParseErrorMessage());
        }

    }

    /**
     * <DL>
     * <DT>Description:</DT>
     * <DD>Format the description of an option</DD>
     * <DT>Date:</DT>
     * <DD>Apr 13, 2016</DD>
     * </DL>
     *
     * @param message
     * @param defaultValue
     * @param validList
     * @return
     */
    protected String formatDescription(final String message, final String defaultValue, final String validList) {
        final String resolvedMessage = String.format(message, defaultValue);
        final String result;
        if (validList != null && !validList.isEmpty()) {
            result = String.format("%1$s \nValid Values: %2$s", resolvedMessage, validList);
        } else {
            result = resolvedMessage;
        }
        return result;
    }

    /**
     * <DL>
     * <DT>Description:</DT>
     * <DD>Getter for the options property</DD>
     * <DT>Date:</DT>
     * <DD>Apr 12, 2016</DD>
     * </DL>
     *
     * @return the value of options field
     */
    public Options getOptions() {
        if (options == null) {
            setOptions(configureOptions());
        }
        return options;
    }

    /**
     * <DL>
     * <DT>Description:</DT>
     * <DD>Getter for the optionValues property</DD>
     * <DT>Date:</DT>
     * <DD>Apr 12, 2016</DD>
     * </DL>
     *
     * @return the value of optionValues field
     */
    public Map<String, Object> getOptionValues() {
        return optionValues;
    }

    /**
     * <DL>
     * <DT>Description:</DT>
     * <DD>Getter for the parseErrorMessage property</DD>
     * <DT>Date:</DT>
     * <DD>Apr 12, 2016</DD>
     * </DL>
     *
     * @return the value of parseErrorMessage field
     */
    public String getParseErrorMessage() {
        return parseErrorMessage;
    }

    /**
     * <DL>
     * <DT>Description:</DT>
     * <DD>
     * Getter for the pinLEDScapeMappings property</DD>
     * <DT>Date:</DT>
     * <DD>Sep 24, 2016</DD>
     * </DL>
     *
     * @return the value of pinLEDScapeMappings field
     */
    public Map<String, PinDescriptor> getPinLEDScapeMappings() {
        return pinLEDScapeMappings;
    }

    /**
     * <DL>
     * <DT>Description:</DT>
     * <DD>Getter for the pinMappings property</DD>
     * <DT>Date:</DT>
     * <DD>Apr 12, 2016</DD>
     * </DL>
     *
     * @return the value of pinMappings field
     */
    public Map<String, PinDescriptor> getPinMappings() {
        return pinMappings;
    }

    /**
     * <DL>
     * <DT>Description:</DT>
     * <DD>Getter for the pinReverseMappings property</DD>
     * <DT>Date:</DT>
     * <DD>Apr 12, 2016</DD>
     * </DL>
     *
     * @return the value of pinReverseMappings field
     */
    public Map<String, PinDescriptor> getPinReverseMappings() {
        return pinReverseMappings;
    }

    /**
     * <DL>
     * <DT>Description:</DT>
     * <DD>Getter for the status property</DD>
     * <DT>Date:</DT>
     * <DD>Apr 12, 2016</DD>
     * </DL>
     *
     * @return the value of status field
     */
    public CLIParseStatus getStatus() {
        return status;
    }

    /**
     * <DL>
     * <DT>Description:</DT>
     * <DD>Load the pin mappings from the CSV file</DD>
     * <DT>Date:</DT>
     * <DD>Apr 12, 2016</DD>
     * </DL>
     */
    protected boolean loadPinMappings() {
        boolean success = false;
        try {
            final InputStream in = ValidateGPIO.class.getClass().getResourceAsStream(CAPE_PIN_MAP);
            final Reader reader = new BufferedReader(new InputStreamReader(in));

            final Iterable<CSVRecord> records = CSVFormat.EXCEL.withHeader().parse(reader);
            for (final CSVRecord csvRecord : records) {
                final Map<String, String> record = csvRecord.toMap();
                final PinDescriptor pin = new PinDescriptor(record);
                if (pin.getCapePin() != null && !pin.getCapePin().isEmpty()) {
                    getPinMappings().put(pin.getCapePin(), pin);
                    if (!pin.getLEDScapePin().isEmpty()) {
                        getPinLEDScapeMappings().put(pin.getLEDScapePin(), pin);
                    }
                }
                getPinReverseMappings().put(pin.getBulldogHeaderPin(), pin);
            }
            success = true;
        } catch (final IOException ex) {
            log.error("caught {} Error : ", ex.getClass().getSimpleName() //$NON-NLS-1${0xD}
                , ex);
        }
        return success;
    }

    /**
     * <DL>
     * <DT>Description:</DT>
     * <DD>convert the pins passed from the command line to bulldog library representation. if the pin is in the bulldog
     * format already resolve the cape pin for it. if an error occurs it will be stored in the ParseErrorMessage
     * property and the Status will be set to INVALID_OPTION</DD>
     * <DT>Date:</DT>
     * <DD>Apr 12, 2016</DD>
     * </DL>
     *
     * @param pins
     *            the list of the resolved bulldog library pins (to be updated)
     * @param pinsToTest
     *            the pins from the command line
     * @return <DL>
     *         <DT><code>true</code></DT>
     *         <DD>successfully parsed and mapped the incomming pins</DD>
     *         <DT><code>false</code></DT>
     *         <DD>Otherwise</DD>
     *         </DL>
     */
    protected boolean loadPins(final List<String> pins, final List<String> pinsToTest) {
        boolean valid = true;
        for (final String capePinIn : pinsToTest) {
            final String capePin = capePinIn.trim();
            if (capePin.contains("_")) { //we got a header pin directly P[89]_[0-4]*[0-9]
                if (capePin.toUpperCase().matches(PIN_LIST_HEADER_REGEX)) {
                    pins.add(capePin);
                }
            } else if (capePin.contains("L")) { //we got a LEDScpe pin directly L[0-4]{0,1}[0-9]
                if (getPinLEDScapeMappings().containsKey(capePin)) {
                    pins.add(getPinLEDScapeMappings().get(capePin).getBulldogHeaderPin());
                } else {
                    setParseErrorMessage(String.format(PARSE_ERROR_FMT, "invalid pin: " + capePin));
                    log.error(getParseErrorMessage());
                    setStatus(CLIParseStatus.INVALID_OPTION);
                    valid = false;
                    break;
                }
            } else {
                if (getPinMappings().containsKey(capePin)) {
                    pins.add(getPinMappings().get(capePin).getBulldogHeaderPin());
                } else {
                    setParseErrorMessage(String.format(PARSE_ERROR_FMT, "invalid pin: " + capePin));
                    log.error(getParseErrorMessage());
                    setStatus(CLIParseStatus.INVALID_OPTION);
                    valid = false;
                    break;
                }
            }
        }
        return valid;
    }

    /**
     * <DL>
     * <DT>Description:</DT>
     * <DD>process command line arguments</DD>
     * <DT>Date:</DT>
     * <DD>Apr 12, 2016</DD>
     * </DL>
     *
     * @param args
     */
    private void parseArguments(final String[] args) {
        final CommandLineParser parser = new DefaultParser();
        // parse the command line arguments
        setStatus(CLIParseStatus.SUCCESS);
        if (args.length == 0) { //if nothing is passed assume help
            args[0] = "-h";
        }
        CommandLine line;
        try {
            line = parser.parse(getOptions(), args);
            if (line.hasOption(HELP_ID)) {
                usage(ValidateGPIO.class.getName());
                setParseErrorMessage(""); //$NON-NLS-1$
                setStatus(CLIParseStatus.HELP);
            } else {
                if (line.hasOption(DIRECTION_SHORT)) {
                    processListOption(line, DIRECTION_SHORT, DIRECTION, DIRECTION_VALID, true);
                } else {
                    getOptionValues().put(DIRECTION_SHORT, DIRECTION_DEFAULT);
                }
                if (line.hasOption(QUERY_FORMAT_SHORT)) {
                    processListOption(line, QUERY_FORMAT_SHORT, QUERY_FORMAT, QUERY_FORMAT_VALID, true);
                } else {
                    getOptionValues().put(QUERY_FORMAT_SHORT, QUERY_FORMAT_DEFAULT);
                }
                PinDescriptor.setQueryFormat(QueryOutputFormat.valueOf(getOptionValues().get(QUERY_FORMAT_SHORT)
                    .toString()));
                processPins(line, new ArrayList<String>(), PIN_LIST_SHORT, PIN_LIST_DEFAULT);
                getOptionValues().put(PIN_LIST_EXISTS, line.hasOption(PIN_LIST_SHORT));
                if (line.hasOption(QUERY_SHORT)) {
                    processPins(line, new ArrayList<String>(), QUERY_SHORT,
                        Arrays.asList(QUERY_DEFAULT.split(LIST_SEPARATOR_REGEX)));
                    processQuery();
                }
            }

        } catch (final ParseException ex) {
            if (getStatus() != CLIParseStatus.HELP) {
                // TODO Auto-generated catch block
                log.error("caught {} Error : ", ex.getClass().getSimpleName() //$NON-NLS-1${0xD}
                    , ex);
                setParseErrorMessage(String.format(PARSE_ERROR_FMT, ex.toString()));
                setStatus(CLIParseStatus.GENERAL_ERROR);
                usage(ValidateGPIO.class.getName());
            }
        }
    }

    /**
     * <DL>
     * <DT>Description:</DT>
     * <DD>process and option, validated against a default list</DD>
     * <DT>Date:</DT>
     * <DD>Apr 12, 2016</DD>
     * </DL>
     *
     * @param line
     *            parsed command line
     * @param option_short
     *            rhe short option id
     * @param option_long
     *            the long option id
     * @param validList
     *            the list of valid values
     * @param upperCase
     *            <DL>
     *            <DT><code>true</code></DT>
     *            <DD>test starts with as upper case as well equals case insensitive</DD>
     *            <DT><code>false</code></DT>
     *            <DD>test starts with as lower case as well equals case insensitive</DD>
     *            </DL>
     */
    protected void processListOption(final CommandLine line, final String option_short, final String option_long,
        final List<String> validList, final boolean upperCase) {
        final String passedOption = line.getOptionValue(option_short).trim();
        boolean valid = false;
        for (final String validOption : validList) {
            final String testOption = upperCase ? passedOption.toUpperCase() : passedOption.toLowerCase();
            if (testOption.startsWith(validOption) || passedOption.equalsIgnoreCase(validOption)) {
                getOptionValues().put(option_short, validOption);
                valid = true;
                break;
            }
        }
        if (!valid) {
            setParseErrorMessage(String.format(PARSE_ERROR_FMT, "invalid " + option_long + " passed, valid "
                + option_long + " values are: " + validList.toString()));
            setStatus(CLIParseStatus.INVALID_OPTION);
        }
    }

    /**
     * <DL>
     * <DT>Description:</DT>
     * <DD>TODO add processPins description</DD>
     * <DT>Date:</DT>
     * <DD>Apr 13, 2016</DD>
     * </DL>
     *
     * @param line
     * @param pins
     * @param optionKey
     * @param defaultPins
     */
    protected void processPins(final CommandLine line, final List<String> pins, final String optionKey,
        final List<String> defaultPinsIn) {
        final List<String> defaultPins = testListForAll(defaultPinsIn);
        // TODO add support for (def)ault argument use the default value(s).
        if (line.hasOption(optionKey)) {
            final List<String> pinsToTest = testListForAll(Arrays.asList(line.getOptionValues(optionKey)));
            loadPins(pins, pinsToTest);
        } else {
            loadPins(pins, defaultPins);
        }
        getOptionValues().put(optionKey, pins);
    }

    /**
     * <DL>
     * <DT>Description:</DT>
     * <DD>TODO add processQuery description</DD>
     * <DT>Date:</DT>
     * <DD>Apr 13, 2016</DD>
     * </DL>
     */
    protected void processQuery() {
        @SuppressWarnings("unchecked")
        final List<String> pins = (List<String>) getOptionValues().get(QUERY_SHORT);
        final String sep = PinDescriptor.getSeparatorFormat();
        final String header = PinDescriptor.getHeaderFormat();
        final String trailer = PinDescriptor.getTrailerFormat();
        final StringBuilder sb = new StringBuilder(header);
        boolean first = true;
        for (final String pinName : pins) {
            if (!first) {
                sb.append(sep);
            }
            sb.append(getPinReverseMappings().get(pinName));
            first = false;
        }
        sb.append(trailer);
        log.trace(sb.toString());
        System.out.println(sb.toString());
    }

    /**
     * <DL>
     * <DT>Description:</DT>
     * <DD>Setter for the options property</DD>
     * <DT>Date:</DT>
     * <DD>Apr 12, 2016</DD>
     * </DL>
     *
     * @param aOptions
     *            new value for the options property
     */
    public void setOptions(final Options aOptions) {
        options = aOptions;
    }

    /**
     * <DL>
     * <DT>Description:</DT>
     * <DD>Setter for the optionValues property</DD>
     * <DT>Date:</DT>
     * <DD>Apr 12, 2016</DD>
     * </DL>
     *
     * @param aOptionValues
     *            new value for the optionValues property
     */
    public void setOptionValues(final Map<String, Object> aOptionValues) {
        optionValues = aOptionValues;
    }

    /**
     * <DL>
     * <DT>Description:</DT>
     * <DD>Setter for the parseErrorMessage property</DD>
     * <DT>Date:</DT>
     * <DD>Apr 12, 2016</DD>
     * </DL>
     *
     * @param aParseErrorMessage
     *            new value for the parseErrorMessage property
     */
    public void setParseErrorMessage(final String aParseErrorMessage) {
        parseErrorMessage = aParseErrorMessage;
    }

    /**
     * <DL>
     * <DT>Description:</DT>
     * <DD>
     * Setter for the pinLEDScapeMappings property</DD>
     * <DT>Date:</DT>
     * <DD>Sep 24, 2016</DD>
     * </DL>
     *
     * @param pinLEDScapeMappings
     *            new value for the pinLEDScapeMappings property
     */
    public void setPinLEDScapeMappings(final Map<String, PinDescriptor> pinLEDScapeMappings) {
        this.pinLEDScapeMappings = pinLEDScapeMappings;
    }

    /**
     * <DL>
     * <DT>Description:</DT>
     * <DD>Setter for the pinMappings property</DD>
     * <DT>Date:</DT>
     * <DD>Apr 12, 2016</DD>
     * </DL>
     *
     * @param aPinMappings
     *            new value for the pinMappings property
     */
    public void setPinMappings(final Map<String, PinDescriptor> aPinMappings) {
        pinMappings = aPinMappings;
    }

    /**
     * <DL>
     * <DT>Description:</DT>
     * <DD>Setter for the pinReverseMappings property</DD>
     * <DT>Date:</DT>
     * <DD>Apr 12, 2016</DD>
     * </DL>
     *
     * @param aPinReverseMappings
     *            new value for the pinReverseMappings property
     */
    public void setPinReverseMappings(final Map<String, PinDescriptor> aPinReverseMappings) {
        pinReverseMappings = aPinReverseMappings;
    }

    /**
     * <DL>
     * <DT>Description:</DT>
     * <DD>Setter for the status property</DD>
     * <DT>Date:</DT>
     * <DD>Apr 12, 2016</DD>
     * </DL>
     *
     * @param aStatus
     *            new value for the status property
     */
    public void setStatus(final CLIParseStatus aStatus) {
        status = aStatus;
    }

    /**
     * <DL>
     * <DT>Description:</DT>
     * <DD>TODO add testListForAll description</DD>
     * <DT>Date:</DT>
     * <DD>Apr 13, 2016</DD>
     * </DL>
     *
     * @param workSet
     * @return
     */
    protected List<String> testListForAll(final List<String> workList) {
        final Set<String> workSet = new HashSet<String>(workList);
        final List<String> resolvedList;
        if (workSet.contains(ALL)) {
            resolvedList = new ArrayList<>(getPinReverseMappings().keySet());
        } else {
            resolvedList = workList;
        }
        return resolvedList;
    }

    /**
     * <DL>
     * <DT>Description:</DT>
     * <DD>Print the help message for the passed class</DD>
     * <DT>Date:</DT>
     * <DD>Oct 12, 2010</DD>
     * </DL>
     *
     * @param mainClassName
     *            the name of the executing class
     */
    protected void usage(final String mainClassName) {
        final HelpFormatter formatter = new HelpFormatter();
        formatter.setWidth(120);
        final StringBuilder line = new StringBuilder().append("java ") //$NON-NLS-1$
                .append(mainClassName);
        // formatter.
        formatter.printHelp(line.toString(), getOptions(), true);
    }
}
