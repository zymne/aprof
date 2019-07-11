package com.devexperts.aprof.transformer;

import org.objectweb.asm.MethodVisitor;

public class ModTrackerMethodTransformer extends MethodVisitor {

    public ModTrackerMethodTransformer(int i, MethodVisitor methodVisitor) {
        super(i, methodVisitor);
    }
}
