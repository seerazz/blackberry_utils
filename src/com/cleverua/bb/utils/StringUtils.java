package com.cleverua.bb.utils;

import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.Enumeration;
import java.util.Vector;

import net.rim.device.api.util.Comparator;
import net.rim.device.api.util.StringComparator;

public class StringUtils {

    public static StrComparator STRING_COMPARATOR = new StrComparator();
    
    // supported by BB encodings
    public static final String ENCODING_ISO_8859_1 = "ISO-8859-1"; // a default one
    public static final String ENCODING_US_ASCII   = "US-ASCII";
    public static final String ENCODING_UTF_16BE   = "UTF-16BE";
    public static final String ENCODING_UTF_16LE   = "UTF-16LE";
    public static final String ENCODING_UTF_8      = "UTF-8";
    
    public static final String DEFAULT_ENCODING = ENCODING_ISO_8859_1;
    
    private static final String EMPTY = "";
    private static final String EMPTY_ARRAY_REPRESENTATION = "[]";
    private static final String NULL_REPRESENTATION = "null";

    public static boolean isBlank(String str) {
        return (str == null || str.length() == 0);
    }

    public static boolean isNotBlank(String str) {
        return !isBlank(str);
    }

    public static String safe(String str) {
        return str == null ? EMPTY : str;
    }

    public static String safe(String str, String defaultValue) {
        return str == null ? defaultValue : str;
    }

    public static int linesCount(String str) {
        if (str.length() == 0) {
            return 0;
        }
        
        int result = 1;
        for (int indx = 0; (indx = str.indexOf('\n', indx)) != -1; indx++, result++);
        
        return result;
    }

    // http://supportforums.blackberry.com/rim/board/message?board.id=java_dev&message.id=34183
    public static String replaceAll(String source, String pattern, String replacement) {
        if (source == null) {
            return EMPTY;
        }

        StringBuffer sb = new StringBuffer();
        int idx = -1;
        int patIdx = 0;

        while ((idx = source.indexOf(pattern, patIdx)) != -1) {
            sb.append(source.substring(patIdx, idx));
            sb.append(replacement);
            patIdx = idx + pattern.length();
        }
        sb.append(source.substring(patIdx));

        return sb.toString();
    }

    public static String removeBefore(String str, String substrToDelete) {
        if (str == null) {
            return str;
        }

        int index = str.indexOf(substrToDelete);
        if (index == -1) {
            return str;
        }
        return str.substring(index + substrToDelete.length());
    }

    /**
     * Returns a string containing the tokens joined by delimiters.
     * 
     * @param tokens an {@link Enumeration} of objects to be joined. 
     * Strings will be formed from the objects by calling <code>object.toString()</code>.
     * 
     * @see {@link java.util.Hashtable#elements() Hashtable.elements()}, 
     * {@link java.util.Hashtable#keys() Hashtable.keys()}, 
     * {@link java.util.Vector#elements() Vector.elements()}
     */
    public static String join(String delimiter, Enumeration tokens) {
        StringBuffer sb = new StringBuffer();
        boolean firstTime = true;
        while (tokens.hasMoreElements()) {
            Object token = tokens.nextElement();
            if (firstTime) {
                firstTime = false;
            } else {
                sb.append(delimiter);
            }
            sb.append(token);
        }
        return sb.toString();
    }
    
    /**
     * Returns a string containing the tokens joined by delimiters.
     * 
     * @param tokens an Array of objects to be joined. 
     * Strings will be formed from the objects by calling <code>object.toString()</code>.
     */
    public static String join(String delimiter, Object[] tokens) {
        StringBuffer sb = new StringBuffer();
        boolean firstTime = true;
        final int len = tokens.length;
        for (int i = 0; i < len; i++) {
            Object token = tokens[i];
            if (firstTime) {
                firstTime = false;
            } else {
                sb.append(delimiter);
            }
            sb.append(token);
        }
        return sb.toString();
    }
    
    /**
     * Similar to a modern Java <code>String.spit(String expression)</code>, 
     * the result does not include trailing empty strings:
     * 
     * <p>
     * <code>StringUtils.split("asdf", "as") => ["", "df"]</code><br />
     * <b>BUT</b><br/>
     * <code>StringUtils.split("asdf", "df") => ["as"]</code>
     * <p/>
     * 
     * <p>Other "extreme" examples:</p>
     * <ul>
     * <li><code>StringUtils.split("", "")       => []</code></li>
     * <li><code>StringUtils.split("", "s")      => []</code></li>
     * <li><code>StringUtils.split("s", "")      => ["s"]</code></li>
     * <li><code>StringUtils.split("sss", "")    => ["s", "s", "s"]</code></li>
     * <li><code>StringUtils.split("s", "s")     => []</code></li>
     * <li><code>StringUtils.split("sss", "sss") => []</code></li>
     * </ul>
     * 
     * @param str - a string to be split
     * @param delimiter - a string to split with
     * @return An array of strings. The array will be empty if str is empty.
     *
     * @throws NullPointerException if delimiter or str is null
     */
    public static String[] split(String str, String delimiter) {
        final int strLen = str.length();
        if (strLen == 0) {
            return new String[] {};
        }
        
        final int delimiterLen = delimiter.length();
        if (delimiterLen == 0) {
            final String[] result = new String[strLen];
            for (int i = 0; i < strLen; i++) {
                result[i] = str.substring(i, i + 1);
            }
            return result;
        }
        
        final Vector accumulator = new Vector();
        
        int offset = 0;
        while (true) {
            final int delimiterPosition = str.indexOf(delimiter, offset);
            if (delimiterPosition < 0) {
                if (offset == 0) {
                    // not even one item found, do nothing
                } else {
                    // the last item
                    accumulator.addElement(str.substring(offset, strLen));
                }
                break;
            } else {
                accumulator.addElement(str.substring(offset, delimiterPosition));
                offset = delimiterPosition + delimiterLen;
            }
        }
        
        final int accumulatorLen = accumulator.size();
        final String[] result = new String[accumulatorLen];
        for (int i = 0; i < accumulatorLen; i++) {
            result[i] = (String) accumulator.elementAt(i);
        }
        
        int nonEmptyElementIndex = -1;
        for (int i = accumulatorLen - 1; i >= 0; i--) {
            if (result[i].length() > 0) {
                nonEmptyElementIndex = i;
                break;
            }
        }
        
        final int fixedResultLen = nonEmptyElementIndex + 1;
        if (fixedResultLen == accumulatorLen) {
            return result;
        } else {
            final String[] fixedResult = new String[fixedResultLen];
            for (int i = 0; i < fixedResultLen; i++) {
                fixedResult[i] = result[i];
            }
            return fixedResult;
        }
    }
    
    /**
     * @param array
     * @return human readable string representation of array, e.g.:
     * <p>
     * <code>arrayToString(new String[] { "a", "b" }) => ["a", "b"]</code><br />
     * <code>arrayToString(new String[] { "a", null }) => ["a", null]</code><br />
     * <code>arrayToString(new String[] { "" }) => [""]</code><br />
     * <code>arrayToString(new String[] {}) => []</code>
     * </p>
     */
    public static String arrayToString(Object[] array) {
        if (array == null) {
            return NULL_REPRESENTATION;
        } else if (array.length == 0) {
            return EMPTY_ARRAY_REPRESENTATION;
        } else {
            StringBuffer sb = new StringBuffer();
            boolean firstTime = true;
            final int len = array.length;
            for (int i = 0; i < len; i++) {
                if (firstTime) {
                    firstTime = false;
                    sb.append('[');
                } else {
                    sb.append(',').append(' ');
                }
                sb.append(toHumanReadableString(array[i]));
            }
            return sb.append(']').toString();
        }
    }
    
    /**
     * This method is useful for custom {@link Object#toString()} implementations.
     * 
     * <p>
     * If obj is null, then plain "null" string (without quotation marks) is returned, 
     * otherwise the returned is equivalent to <code>'"' + obj.toString()+ '"'</code>. 
     * </p>
     * 
     * @param obj - an {@link Object}
     * @return human readable string representation of the obj, e.g.:
     * 
     * <p>
     * <code>toHumanReadableString(null) => null</code><br />
     * <code>toHumanReadableString("") => ""</code><br />
     * <code>toHumanReadableString("Hello!") => "Hello!"</code>
     * </p>
     */
    public static String toHumanReadableString(Object obj) {
        return obj == null ? NULL_REPRESENTATION : '"' + obj.toString()+ '"';
    }
    
    /**
     * Constructs a String using the data read from the passed InputStream.
     * Data is read using a 1024-chars buffer.
     * Each char is created using the default BlackBerry encoding (ISO-8859-1).
     * 
     * @param in - InputStream to read data from.
     * @return String created using the byte data read from the passed InputStream.
     * @throws IOException if an I/O error occurs.
     */
    public static String getStringFromStream(InputStream in) throws IOException {
        return getStringFromStream(in, null);
    }
    
    /**
     * Constructs a String using the data read from the passed InputStream.
     * Data is read using a 1024-chars buffer. Each char is created using the passed 
     * encoding from one or more bytes.
     * 
     * <p>If passed encoding is null, then the default BlackBerry encoding (ISO-8859-1) is used.</p>
     * 
     * BlackBerry platform supports the following character encodings:
     * <ul>
     * <li>"ISO-8859-1"</li>
     * <li>"UTF-8"</li>
     * <li>"UTF-16BE"</li>
     * <li>"UTF-16LE"</li>
     * <li>"US-ASCII"</li>
     * </ul>
     * 
     * @param in - InputStream to read data from.
     * @param encoding - String representing the desired character encoding, can be null.
     * @return String created using the char data read from the passed InputStream.
     * @throws IOException if an I/O error occurs.
     * @throws UnsupportedEncodingException if encoding is not supported.
     */
    public static String getStringFromStream(InputStream in, String encoding) throws IOException {
        InputStreamReader reader;
        if (encoding == null) {
            reader = new InputStreamReader(in);
        } else {
            reader = new InputStreamReader(in, encoding);            
        }
        
        StringBuffer sb = new StringBuffer();
        
        final char[] buf = new char[1024];
        int len;
        while ((len = reader.read(buf)) > 0) {
            sb.append(buf, 0, len);
        }
        
        return sb.toString();
    }
    
    private static class StrComparator implements Comparator {

        private static final StringComparator STRING_COMPARATOR = StringComparator.getInstance(true);

        public int compare(Object o1, Object o2) {
            return STRING_COMPARATOR.compare(o1.toString(), o2.toString());
        }
    }
}
