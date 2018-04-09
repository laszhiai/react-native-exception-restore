package jacky.stack;

import jacky.internal.Engine;
import jacky.sourcemap.OriginalPosition;
import jacky.sourcemap.SourceMap;
import jacky.sourcemap.SourceMapConsumer;
import jdk.nashorn.api.scripting.JSObject;
import jdk.nashorn.api.scripting.ScriptObjectMirror;

import javax.script.ScriptException;
import java.io.File;
import java.io.FileInputStream;
import java.util.ArrayList;
import java.util.List;

public class StackTraceParser {
    private final Engine engine;

    public StackTraceParser(Engine engine) {
        this.engine = engine;
    }

    public static StackTraceParser Create() throws ScriptException {
        Engine engine = Engine.create();
        engine.evalResource("stacktrace-parser.js");
        return new StackTraceParser(engine);
    }

    public List<StackFrame> parseErrorStack(String json) throws ScriptException {
        JSObject stack = engine.invokeFunction("StackTraceParser",json);
        List<StackFrame> unglyStack = new ArrayList<>();
        ((ScriptObjectMirror) stack).forEach((key, value) -> unglyStack.add(StackFrame.unglyStack()
                .file(((ScriptObjectMirror) value).get("file").toString())
                .methodName(((ScriptObjectMirror) value).get("methodName").toString())
                .lineNumber(((Double) ((ScriptObjectMirror) value).getMember("lineNumber")).intValue())
                .column(((Double) ((ScriptObjectMirror) value).getMember("column")).intValue())
                .bulid()));
        return unglyStack;
    }

    public static String Parser(String source, String error) {
        System.out.println("===start parse===");
        try {
            FileInputStream sourceMapStream = new FileInputStream(new File(source));
            FileInputStream errorStream = new FileInputStream(new File(error));
            StringBuilder stringBuilder = new StringBuilder();
            byte[] buf = new byte[1024];
            int len = 0;
            while((len=errorStream.read(buf))!=-1){
                stringBuilder.append(new String(buf, 0, len));
            }
            String string = stringBuilder.toString();
            StackTraceParser trace = StackTraceParser.Create();
            List<StackFrame> stackFrames = trace.parseErrorStack(string);

            SourceMap sourceMap = SourceMap.create();
            SourceMapConsumer smc = sourceMap.newSourceMapConsumer(sourceMapStream);
            for (StackFrame frame : stackFrames) {
                OriginalPosition originalPosition = smc.originalPositionFor(frame.lineNumber, frame.column);
                frame.file = originalPosition.source;
                frame.methodName = originalPosition.name;
                frame.lineNumber = originalPosition.line.intValue();
                frame.column = originalPosition.column.intValue();

            }

            String stackLines = makeStackLines(stackFrames);
            System.out.println(stackLines);
            System.out.println("===parser OK===");

            return stackLines;
        } catch (Exception e) {
            System.out.println("===error during parsing===");
            e.printStackTrace();
        }
        return "";
    }

    private static String makeStackLines(List<StackFrame> stackFrames) {
        StringBuilder stackLines = new StringBuilder();
        for (int i = 0; i < stackFrames.size(); i++) {
            StackFrame frame = stackFrames.get(i);
            String line = i + ". " + frame.file + " : " +frame.methodName
                    + ", Line: " + frame.lineNumber + ", Column: " + frame.column + "\n\n";
            stackLines.append(line);
        }
        return stackLines.toString();
    }
}
