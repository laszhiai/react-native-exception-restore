package jacky.stack;

import java.util.Objects;

public class StackFrame {
    public String file;
    public String methodName;
    public int lineNumber;
    public int column;

    public StackFrame(Builder builder) {
        this.file = builder.file;
        this.methodName = builder.methodName;
        this.lineNumber = builder.lineNumber;
        this.column = builder.column;
    }

    public static Builder unglyStack() {
        return new Builder();
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        StackFrame that = (StackFrame) o;
        return Objects.equals(file, that.file) &&
                Objects.equals(methodName, that.methodName) &&
                Objects.equals(lineNumber, that.lineNumber) &&
                Objects.equals(column, that.column);
    }

    @Override
    public int hashCode() {

        return Objects.hash(file, methodName, lineNumber, column);
    }

    @Override
    public String toString() {
        return "StackFrame{" +
                "file='" + file + '\'' +
                ", methodName='" + methodName + '\'' +
                ", lineNumber=" + lineNumber +
                ", column=" + column +
                '}';
    }

    public static class Builder{
        private String file = "";
        private String methodName = "";
        private int lineNumber;
        private int column;

        private Builder(){}

        public Builder file(String file) {
            this.file = file;
            return this;
        }

        public Builder methodName(String methodName) {
            this.methodName = methodName;
            return this;
        }

        public Builder lineNumber(int lineNumber) {
            this.lineNumber = lineNumber;
            return this;
        }

        public Builder column(int column) {
            this.column = column;
            return this;
        }

        public StackFrame bulid() {return new StackFrame(this);}
    }
}
