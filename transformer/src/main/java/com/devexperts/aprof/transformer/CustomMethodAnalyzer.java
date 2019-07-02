package com.devexperts.aprof.transformer;

import org.objectweb.asm.commons.GeneratorAdapter;

public class CustomMethodAnalyzer extends AbstractMethodVisitor {

    public CustomMethodAnalyzer(GeneratorAdapter mv, Context context, int classVersion) {
        super(mv, context, classVersion);
    }

    protected void visitMarkDeclareLocationStack() {

    }

    protected void visitStartInvokedMethod() {

    }

    protected void visitReturnFromInvokedMethod() {

    }

    protected void visitEndInvokedMethod() {

    }

    protected void visitTrackedMethodInsn(int opcode, String owner, String name, String desc, boolean intf) {

    }

    protected void visitObjectInit() {

    }

    protected void visitAllocateBefore(String desc) {

    }

    protected void visitAllocateAfter(String desc) {

    }

    protected void visitAllocateArrayBefore(String desc) {

    }

    protected void visitAllocateArrayAfter(String desc) {

    }

    protected void visitAllocateArrayMulti(String desc) {

    }

    protected void visitAllocateReflect(boolean cloneInvocation) {

    }

    protected void visitAllocateReflectVClone() {

    }
}
