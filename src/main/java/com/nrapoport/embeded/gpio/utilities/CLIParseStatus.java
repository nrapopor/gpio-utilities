/**
 * @author nrapopor - Nick Rapoport
 * @copyright Copyright 2010 ( Nov 19, 2010 ) Nick Rapoport all rights reserved.
 */
package com.nrapoport.embeded.gpio.utilities;

/**
 * <DL>
 * <DT>Description:</DT>
 * <DD>this enum describes various states of the command line parsing. Whether it succeeded or failed, was run with
 * insufficient arguments, or with the help option.
 * <DL>
 * <DT>SUCCESS</DT>
 * <DD>The command line completed successfully</DD>
 * <DT>INVALID_OPTION</DT>
 * <DD>An error was encountered processing the command line options</DD>
 * <DT>HELP</DT>
 * <DD>the help option was specified, no other activity performed</DD>
 * </DL>
 *
 * </DD>
 * <DT>Date:</DT>
 * <DD>Nov 19, 2010</DD>
 * </DL>
 *
 * @author nrapopor - Nick Rapoport
 *
 */
public enum CLIParseStatus {
    /**
     * <DL>
     * <DT>SUCCESS</DT>
     * <DD>The command line completed successfully</DD>
     * </DL>
     */
    SUCCESS, /**
              * <DL>
              * <DT>INVALID_OPTION</DT>
              * <DD>An error was encountered processing the command line options</DD>
              * </DL>
              */
    INVALID_OPTION, /**
                     * <DL>
                     * <DT>GENERAL_ERROR</DT>
                     * <DD>An error was encountered preparing the options for processing</DD>
                     * </DL>
                     */
    GENERAL_ERROR, /**
                    * <DL>
                    * <DT>HELP</DT>
                    * <DD>the help option was specified, no other activity performed</DD>
                    * </DL>
                    */
    HELP

}
