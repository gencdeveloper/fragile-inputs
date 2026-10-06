package io.github.gencdeveloper.fragileinputs;

import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

/**
 * A tiny, dependency-free JSON reader — just enough to load the bundled
 * dataset. Keeping the library free of Jackson/Gson avoids version conflicts
 * on a user's test classpath. Not a general-purpose parser.
 */
final class Json {

    private final String s;
    private int i;

    private Json(String text) {
        this.s = text;
    }

    static Object parse(String text) {
        Json p = new Json(text);
        p.ws();
        Object v = p.value();
        p.ws();
        if (p.i != p.s.length()) {
            throw new IllegalStateException("Trailing characters at position " + p.i);
        }
        return v;
    }

    private Object value() {
        char c = s.charAt(i);
        switch (c) {
            case '{': return object();
            case '[': return array();
            case '"': return string();
            case 't': expect("true"); return Boolean.TRUE;
            case 'f': expect("false"); return Boolean.FALSE;
            case 'n': expect("null"); return null;
            default:  return number();
        }
    }

    private Map<String, Object> object() {
        Map<String, Object> m = new LinkedHashMap<>();
        i++; // {
        ws();
        if (peek() == '}') { i++; return m; }
        while (true) {
            ws();
            String key = string();
            ws();
            if (s.charAt(i) != ':') throw err("':'");
            i++;
            ws();
            m.put(key, value());
            ws();
            char c = s.charAt(i++);
            if (c == '}') return m;
            if (c != ',') throw err("',' or '}'");
        }
    }

    private List<Object> array() {
        List<Object> a = new ArrayList<>();
        i++; // [
        ws();
        if (peek() == ']') { i++; return a; }
        while (true) {
            ws();
            a.add(value());
            ws();
            char c = s.charAt(i++);
            if (c == ']') return a;
            if (c != ',') throw err("',' or ']'");
        }
    }

    private String string() {
        if (s.charAt(i) != '"') throw err("'\"'");
        i++;
        StringBuilder b = new StringBuilder();
        while (true) {
            char c = s.charAt(i++);
            if (c == '"') return b.toString();
            if (c == '\\') {
                char e = s.charAt(i++);
                switch (e) {
                    case '"':  b.append('"'); break;
                    case '\\': b.append('\\'); break;
                    case '/':  b.append('/'); break;
                    case 'b':  b.append('\b'); break;
                    case 'f':  b.append('\f'); break;
                    case 'n':  b.append('\n'); break;
                    case 'r':  b.append('\r'); break;
                    case 't':  b.append('\t'); break;
                    case 'u':
                        b.append((char) Integer.parseInt(s.substring(i, i + 4), 16));
                        i += 4;
                        break;
                    default: throw err("valid escape");
                }
            } else {
                b.append(c);
            }
        }
    }

    private Double number() {
        int start = i;
        while (i < s.length() && "+-0123456789.eE".indexOf(s.charAt(i)) >= 0) i++;
        return Double.parseDouble(s.substring(start, i));
    }

    private void expect(String word) {
        if (!s.startsWith(word, i)) throw err("'" + word + "'");
        i += word.length();
    }

    private char peek() {
        return s.charAt(i);
    }

    private void ws() {
        while (i < s.length()) {
            char c = s.charAt(i);
            if (c == ' ' || c == '\t' || c == '\n' || c == '\r') i++;
            else break;
        }
    }

    private IllegalStateException err(String expected) {
        return new IllegalStateException("Expected " + expected + " at position " + i);
    }
}
