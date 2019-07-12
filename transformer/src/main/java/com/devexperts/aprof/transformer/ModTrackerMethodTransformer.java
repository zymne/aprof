package com.devexperts.aprof.transformer;

import org.objectweb.asm.MethodVisitor;
import org.objectweb.asm.Opcodes;
import org.objectweb.asm.Type;
import org.objectweb.asm.commons.GeneratorAdapter;

public class ModTrackerMethodTransformer extends MethodVisitor {

    protected final GeneratorAdapter mv;
    protected final Context context;
    private final int classVersion;

    private int counter;
    private boolean markedReadObject = false;

    public ModTrackerMethodTransformer(GeneratorAdapter mv, Context context, int classVersion) {
        super(TransformerUtil.ASM_API, mv);
        this.mv = mv;
        this.context = context;
        this.classVersion = classVersion;
    }

    @Override
    public void visitCode() {
        mv.visitCode();
        visitMarkCounter();
    }

    protected void visitAllocateBefore(String desc) {
        visitAllocate(desc);
    }

    protected void visitAllocateAfter(String desc) {
    }

    @Override
    public void visitTypeInsn(int opcode, String desc) {
        if(opcode == Opcodes.NEW) {
            visitAllocateBefore(desc);
            mv.visitTypeInsn(opcode, desc);
            //visitAllocateAfter(desc);
        }
        else
            mv.visitTypeInsn(opcode, desc);
    }

    private void visitAllocate(String desc) {
        //LinkedList special check, insert size limit check if method increment collection size
        visitMarkCheckCollectionSizeLimit(desc);
    }

    //find out if the method eligible for size limit checks (method increments collection size), if so inject size limits check
    //TODO: avoid injecting before throw new Exception in 'add' method
    protected void visitMarkCheckCollectionSizeLimit(String desc) {

        String locationMethod = context.getLocationMethod();
        String locationClass = context.getLocationClass();
        String locationDesc = context.getLocationDesc();

        if(locationClass !=null && locationClass.equals("java.util.LinkedList") && locationMethod != null)
        {

            if(locationMethod.equals("addFirstImpl")
                    || locationMethod.equals("addLastImpl")
                    || ((locationMethod.equals("add") && (locationDesc.equals("(ILjava/lang/Object;)V")))))
            {
                String owner = context.getLocationClass().replace('.', '/');
                mv.visitVarInsn(Opcodes.ALOAD, 0);
                mv.visitFieldInsn(Opcodes.GETFIELD, owner, "size", "I");
                mv.visitMethodInsn(Opcodes.INVOKESTATIC, TransformerUtil.LOCATION_STACK, "checkCollectionSizeLimit", "(I)V", false);

//					Label afterSizeCheck = new Label();
//
//					mv.visitVarInsn(Opcodes.ALOAD, 0);
//					String owner = context.getLocation();
//					mv.visitFieldInsn(Opcodes.GETFIELD, owner, "size", "I");
//					mv.visitIntInsn(Opcodes.SIPUSH, 1000);
//					mv.visitInsn(Opcodes.IREM);
//					mv.visitJumpInsn(Opcodes.IFNE, afterSizeCheck);
//					//* Inside IF block*//
//					mv.visitMethodInsn(Opcodes.INVOKESTATIC, "java/lang/Thread", "currentThread", "()Ljava/lang/Thread;", false);
//					mv.visitMethodInsn(Opcodes.INVOKEVIRTUAL, "java/lang/Thread", "getStackTrace", "()[Ljava/lang/StackTraceElement;", false);
//					//**//
//					mv.visitLabel(afterSizeCheck);
            }
            else if(locationMethod.equals("readObject")) {
                if(!markedReadObject) {

                    String owner = context.getLocationClass().replace('.', '/');
                    mv.visitVarInsn(Opcodes.ALOAD, 0);
                    mv.visitFieldInsn(Opcodes.GETFIELD, owner, "size", "I");
                    mv.visitMethodInsn(Opcodes.INVOKESTATIC, TransformerUtil.LOCATION_STACK, "checkCollectionSizeLimitGT", "(I)V", false);
                    markedReadObject = true;
                }
            }
            else if(locationMethod.equals("addAll")) {
                if(desc.equals("java/util/LinkedList$Link")) {
                    //mv.visitIincInsn(counter, 1);
                    mv.iinc(counter, 1);
                    mv.loadLocal(counter);
                    //mv.visitVarInsn(Opcodes.ILOAD, counter);
                    mv.visitMethodInsn(Opcodes.INVOKESTATIC, TransformerUtil.LOCATION_STACK, "checkCollectionSizeLimit", "(I)V", false);
                }
            }
        }
    }

    //TODO: change method name
    protected void visitMarkCounter() {
        String locationMethod = context.getLocationMethod();
        String locationClass = context.getLocationClass();

        if(locationClass != null) {
            String owner = locationClass.replace('.', '/');
            // Inject counter
            if (owner.equals("java/util/LinkedList") && locationMethod != null) {
                if (locationMethod.equals("addAll")) {
                    mv.visitVarInsn(Opcodes.ALOAD, 0);
                    mv.visitFieldInsn(Opcodes.GETFIELD, owner, "size", "I");
                    //make sure counter created each time we enter addAll/readObject and not created otherwise
                    counter = mv.newLocal(Type.INT_TYPE);
                    // load value from the stack and save it in the counter variable
                    mv.storeLocal(counter);
                    //mv.visitVarInsn(Opcodes.ISTORE, counter);
                }
            }
            else if(owner.equals("java/util/LinkedList$LinkIterator") && locationMethod != null) {
                //TODO: consider moving injection right before/after 'new' operator
                //Inject 'list.size' tracker for LinkIterator
                if (locationMethod.equals("add")) {
                    mv.visitVarInsn(Opcodes.ALOAD, 0);
                    mv.visitFieldInsn(Opcodes.GETFIELD, owner, "list", "Ljava/util/LinkedList;");
                    mv.visitFieldInsn(Opcodes.GETFIELD, owner, "size", "I");
                    mv.visitMethodInsn(Opcodes.INVOKESTATIC, TransformerUtil.LOCATION_STACK, "checkCollectionSizeLimit", "(I)V", false);
                }
            }
        }

    }


}
