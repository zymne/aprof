package com.devexperts.aprof.transformer;

import org.objectweb.asm.MethodVisitor;
import org.objectweb.asm.Opcodes;
import org.objectweb.asm.commons.GeneratorAdapter;

public class ModTrackerMethodTransformer extends MethodVisitor {

    protected final GeneratorAdapter mv;
    protected final Context context;
    private final int classVersion;

    public ModTrackerMethodTransformer(GeneratorAdapter mv, Context context, int classVersion) {
        super(TransformerUtil.ASM_API, mv);
        this.mv = mv;
        this.context = context;
        this.classVersion = classVersion;
    }

    @Override
    public void visitCode() {
        mv.visitCode();
    }

    protected void visitAllocateBefore(String desc) {

    }

    protected void visitAllocateAfter(String desc) {
    }

    @Override
    public void visitTypeInsn(int opcode, String desc) {

        if(opcode == Opcodes.NEW) {
            visitAllocateBefore(desc);
            mv.visitTypeInsn(opcode, desc);
            visitAllocateAfter(desc);
        }
        else
            mv.visitTypeInsn(opcode, desc);

    }


}
