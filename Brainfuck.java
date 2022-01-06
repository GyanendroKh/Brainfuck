import java.util.Arrays;
import java.util.Stack;

public class Brainfuck {

  public static void main(String[] args) {
    String program = "++++++++[>++++[>++>+++>+++>+<<<<-]>+>+>->>+[<]<-]>>.>---.+++++++..+++.>>.<-.<.+++.------.--------.>>+.>++.";

    System.out.println("Program: " + program);

    Brainfuck brainfuck = new Brainfuck(program);
    brainfuck.run();
  }

  private final int[] mMemory;
  private int mMemoryPtr;

  private final String mProgram;
  private int mProgramPtr;

  private final int[] mInput;
  private int mInputPtr;

  private final Stack<Loop> mLoops;

  public Brainfuck(String program, int[] input) {
    mMemory = new int[3000];
    mMemoryPtr = 0;

    mProgram = program;
    mProgramPtr = 0;

    mInput = input;
    mInputPtr = 0;

    mLoops = new Stack<>();

    Arrays.fill(mMemory, 0);
  }

  public Brainfuck(String program) {
    this(program, new int[] {});
  }

  private void increment() {
    mMemory[mMemoryPtr]++;
    mProgramPtr++;
  }

  private void decrement() {
    mMemory[mMemoryPtr]--;
    mProgramPtr++;
  }

  private void right() {
    mMemoryPtr++;
    mProgramPtr++;
  }

  private void left() {
    mMemoryPtr--;
    mProgramPtr++;
  }

  private void output() {
    System.out.print((char) mMemory[mMemoryPtr]);
    mProgramPtr++;
  }

  private void input() {
    mMemory[mMemoryPtr] = mInput[mInputPtr++];
    mProgramPtr++;
  }

  private void loopStart() {
    Loop loop = new Loop(mProgram, mProgramPtr);
    if (mMemory[mMemoryPtr] == 0) {
      mProgramPtr = loop.getEnd() + 1;
    } else {
      if (mLoops.size() == 0 || mLoops.peek().getStart() != loop.getStart()) {
        mLoops.add(loop);
      }
      mProgramPtr++;
    }
  }

  private void loopEnd() {
    if (mMemory[mMemoryPtr] != 0) {
      mProgramPtr = mLoops.peek().getStart();
    } else {
      mLoops.pop();
      mProgramPtr++;
    }
  }

  void run() {
    while (mProgramPtr < mProgram.length()) {
      char operator = mProgram.charAt(mProgramPtr);

      switch (operator) {
        case '>' -> right();
        case '<' -> left();
        case '+' -> increment();
        case '-' -> decrement();
        case '.' -> output();
        case ',' -> input();
        case '[' -> loopStart();
        case ']' -> loopEnd();
        default -> throw new IllegalStateException("Invalid Operator!");
      }
    }
  }

  static class Loop {
    private final String mProgram;
    private final int mStart;
    private final int mEnd;

    public Loop(String program, int startPtr) {
      mProgram = program;
      mStart = startPtr;
      mEnd = findEnd();
    }

    private int findEnd() {
      int loopPassed = 0;

      for (int i = mStart + 1; i < mProgram.length(); i++) {
        if (mProgram.charAt(i) == '[') {
          loopPassed++;
          continue;
        }

        if (mProgram.charAt(i) == ']') {
          if (loopPassed > 0) {
            loopPassed--;
          } else {
            return i;
          }
        }
      }

      throw new IllegalStateException("No Ending for Loop at index " + mStart);
    }

    public int getStart() {
      return mStart;
    }

    public int getEnd() {
      return mEnd;
    }

    @Override
    public String toString() {
      return "Loop{" +
          "mStart=" + mStart +
          ", mEnd=" + mEnd +
          '}';
    }
  }
}
