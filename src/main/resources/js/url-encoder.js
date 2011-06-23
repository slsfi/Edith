var URLEncoder = {

    // characters that are safe to pass through (when encoding or decoding) as-is.  All other characters are encoded as a kind of unicode escape.
    _safeCharacters : "abcdefghijklmnopqrstuvwxyzABCDEFGHIJKLMNOPQRSTUVWXYZ0123456789-_.:",

    _encodedNull : "$N",

    _encodedBlank : "$B",

    encode : function (value) {
        if (value == null)
            return this._encodedNull;

        if (value == "")
            return this._encodedBlank;

        var output = "", dirty = false;

        for (var i = 0; i < value.length; i++) {
            var ch = value.charAt(i);

            if (ch == '$')
            {
                output = output + "$$";
                dirty = true;

                continue;
            }

            if (this._safeCharacters.indexOf(ch) != -1) {
                output = output + ch;

                continue;
            }

            output = output + "$" + this._pad(value.charCodeAt(i).toString(16), 4, "0", 0);
            dirty = true;
        }

        if (dirty)
            return output;
        else
            return value;
    },

    decode : function (value) {
        if (value == this._encodedNull)
            return null;

        if (value == this._encodedBlank)
            return "";

        var output = "", dirty = false;

        for (var i = 0; i < value.length; i++) {
            var ch = value.charAt(i);

            if (ch == '$') {
                dirty = true;

                if (i + 1 < value.length && value.charAt(i + 1) == '$') {
                    output = output + '$';
                    i++;

                    continue;
                }

                if (i + 4 < value.length) {
                    var hex = value.substring(i + 1, i + 5);

                    try {
                        var unicode = parseInt(hex, 16);

                        output = output + String.fromCharCode(unicode);

                        i += 4;

                        continue;
                    }
                    catch (NumberFormatException) {
                        // Ignore.
                    }
                }

                throw this._printf("Input string '{0}' is not valid; the '$' character at position {1} should be followed by another '$' or a four digit hex number (a unicode value).", value, i + 1);
            }

            if (this._safeCharacters.indexOf(ch) == -1) {
                throw this._printf("Input string '{0}' is not valid; the character '{1}' at position {2} is not valid.", value, ch, i + 1);
            }

            output = output + ch;
        }

        if (dirty)
            return output;
        else
            return value;
    },

    _pad : function(value, length, stringToConcatenate, type) {
        return stringToConcatenate || (stringToConcatenate = " "),(length -= value.length) > 0 ? (stringToConcatenate = new Array(Math.ceil(length / stringToConcatenate.length)
                + 1).join(stringToConcatenate)).substr(0, type = !type ? length : type == 1 ? 0 : Math.ceil(length / 2))
                + value + stringToConcatenate.substr(0, length - type) : value;
    },

    _printf : function() {
        var num = arguments.length;
        var oStr = arguments[0];

        for (var i = 1; i < num; i++) {
            var pattern = "\\{" + (i - 1) + "\\}";
            var re = new RegExp(pattern, "g");

            oStr = oStr.replace(re, arguments[i]);
        }

        return oStr;
    }
}
