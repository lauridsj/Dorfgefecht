package dorfprojekt;

import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.lang.reflect.Field;
import java.net.URL;
import java.util.ListIterator;
import java.util.Properties;

import org.apache.logging.log4j.Level;
import org.objectweb.asm.ClassReader;
import org.objectweb.asm.ClassWriter;
import org.objectweb.asm.MethodVisitor;
import org.objectweb.asm.Opcodes;
import org.objectweb.asm.tree.AbstractInsnNode;
import org.objectweb.asm.tree.ClassNode;
import org.objectweb.asm.tree.FieldInsnNode;
import org.objectweb.asm.tree.FrameNode;
import org.objectweb.asm.tree.IincInsnNode;
import org.objectweb.asm.tree.InsnList;
import org.objectweb.asm.tree.InsnNode;
import org.objectweb.asm.tree.JumpInsnNode;
import org.objectweb.asm.tree.LabelNode;
import org.objectweb.asm.tree.LineNumberNode;
import org.objectweb.asm.tree.MethodInsnNode;
import org.objectweb.asm.tree.MethodNode;
import org.objectweb.asm.tree.TypeInsnNode;
import org.objectweb.asm.tree.VarInsnNode;

import com.google.common.base.Charsets;
import com.google.common.collect.HashBiMap;

import cpw.mods.fml.common.FMLCommonHandler;
import net.minecraft.launchwrapper.IClassTransformer;

public class DorfprojektTransformer implements IClassTransformer {

	public static HashBiMap<Integer, String> opcodeMap;
	
	public static final byte DEOBF = 0;
	public static final byte OBF = 1;
	public static final byte SRG = 2;
	
	public static Properties props;
	
	public DorfprojektTransformer()
	{
		initOpcodeMap();
	}
	
	public static String getName(String key, byte b)
	{
		if(b == DEOBF)
		{
			return props.getProperty(key + ".deobf");
		}
		else if(b == OBF)
		{
			return props.getProperty(key + ".obf");
		}
		else if(b == SRG)
		{
			return props.getProperty(key + ".srg");
		}
		else return null;
	}
	
	public static String getSlashedName(String key, byte b)
	{
		return getName(key, b).replace('.', '/');
	}
	
	public boolean doesNameFit(String key, String name)
	{
		return getName(key, OBF).equals(name) || getName(key, DEOBF).equals(name) || getName(key, SRG).equals(name);
	}
	
	public static byte getObfLevel(String key, String name)
	{
		if(getName(key, OBF).equals(name)) return OBF;
		else if(getName(key, DEOBF).equals(name)) return DEOBF;
		else if(getName(key, SRG).equals(name)) return SRG;
		else throw new RuntimeException("Transforming failed: Couldn't find method " + key);
	}

	@Override
	public byte[] transform(String name, String transformedName, byte[] basicClass)
	{
		if(props == null)
		{
			try
			{

				URL urlResource = this.getClass().getResource("/assets/dorfprojekt/asm/transformer.properties");
				InputStream stream = urlResource.openStream();
				InputStreamReader reader = new InputStreamReader(stream, Charsets.UTF_8);
				props = new Properties();
				props.load(reader);
			}
			catch(IOException ex)
			{
				ex.printStackTrace();
			}
		}
		
		if(doesNameFit("Slot", name))
		{
			
			printf("[Dorfprojekt ASM Transformer] Patching %s (%s)\n", name, transformedName);
			printf("[Dorfprojekt ASM Transformer] In MCP: %s\n", DorfprojektLoadingPlugin.IN_MCP);
			return patchSlot(basicClass, getObfLevel("Slot", name));
		}
		/*else if(transformedName.equals("dorfprojekt.Util"))
		{
			System.out.printf("[Dorfprojekt ASM Transformer] Showing %s (%s)\n", name, transformedName);
			showUtil(basicClass);
		}*/
		return basicClass;
	}

	public byte[] patchSlot(byte[] basicClass, byte classObfLevel)
	{
		ClassNode classNode = new ClassNode();
		ClassReader classReader = new ClassReader(basicClass);
		classReader.accept(classNode, 0);

		String slotClass = getSlashedName("Slot", classObfLevel);
		String itemStackClass = getSlashedName("ItemStack", classObfLevel);
		String isItemValidDesc = "(L" + itemStackClass + ";)Z";
		String itemCrownClass = "dorfprojekt/ItemCrown";
		String itemCrownMethod = "isValid";
		String itemCrownMethodDesc = "(L" + slotClass + ";L" + itemStackClass + ";)Z";

		for(MethodNode meth : classNode.methods)
		{
			printf("Method: %s %s\n", meth.name, meth.desc);
			
			if(doesNameFit("Slot.isItemValid", meth.name) && meth.desc.equals(isItemValidDesc))
			{
				
				printInsns(meth);
				InsnList insns = meth.instructions;
				ListIterator<AbstractInsnNode> iter = insns.iterator();
				while(iter.hasNext())
				{
					AbstractInsnNode n = iter.next();
					if(n.getOpcode() == Opcodes.ICONST_1 || n.getOpcode() == Opcodes.IRETURN)
					{
						iter.remove();
					}
				}
				
				AbstractInsnNode lastNode = insns.getLast();
				insns.insertBefore(lastNode, new VarInsnNode(Opcodes.ALOAD, 0));
				insns.insertBefore(lastNode, new VarInsnNode(Opcodes.ALOAD, 1));
				insns.insertBefore(lastNode, new MethodInsnNode(Opcodes.INVOKESTATIC, itemCrownClass, itemCrownMethod, itemCrownMethodDesc, false));
				insns.insertBefore(lastNode, new InsnNode(Opcodes.IRETURN));
				
				printInsns(meth);
			}
		}
		

		ClassWriter writer = new ClassWriter(3);
		classNode.accept(writer);
		
		return writer.toByteArray();
	}



	public static void initOpcodeMap()
	{
		if(opcodeMap == null)
		{
			opcodeMap = HashBiMap.create();
			Field[] fields = Opcodes.class.getDeclaredFields();
			for(Field f : fields)
			{
				if(f.getType().getName().equals("int"))
				{
					try
					{
						int i = (Integer) f.get(null);
						opcodeMap.put(Integer.valueOf(i), f.getName());
					}
					catch(IllegalArgumentException e)
					{
						e.printStackTrace();
					}
					catch(IllegalAccessException e)
					{
						e.printStackTrace();
					}

				}
			}
		}
	}
	
	public void showUtil(byte[] abyte)
	{
		ClassNode classNode = new ClassNode();
		ClassReader classReader = new ClassReader(abyte);
		classReader.accept(classNode, 0);
		
		for(MethodNode meth : classNode.methods)
		{
			printInsns(meth);
		}
	}

	public static void printInsns(MethodNode meth)
	{
		printf("   ---Printing Method instructions: Name: %s | Desc: %s | Signature: %s ---   \n", meth.name, meth.desc, meth.signature);
		for(int i = 0; i < meth.instructions.size(); i++)
		{
			AbstractInsnNode insn = meth.instructions.get(i);
			printf(opcodeMap.get(insn.getOpcode()) + " ");
			if(insn instanceof VarInsnNode)
			{
				VarInsnNode n = (VarInsnNode) insn;
				printf("%s", n.var);
			}
			else if(insn instanceof MethodInsnNode)
			{
				MethodInsnNode n = (MethodInsnNode) insn;
				printf("%s %s %s", n.owner, n.name, n.desc);
			}
			else if(insn instanceof FieldInsnNode)
			{
				FieldInsnNode n = (FieldInsnNode) insn;
				printf("%s %s %s", n.owner, n.name, n.desc);
			}
			else if(insn instanceof JumpInsnNode)
			{
				JumpInsnNode n = (JumpInsnNode) insn;
				printf("%s", n.label.getLabel().toString());
			}
			else if(insn instanceof LabelNode)
			{
				LabelNode n = (LabelNode) insn;
				printf("%s", n.getLabel().toString());
			}
			else if(insn instanceof LineNumberNode)
			{
				LineNumberNode n = (LineNumberNode) insn;
				printf("%s %s", n.line, n.start.getLabel().toString());
			}
			else if(insn instanceof TypeInsnNode)
			{
				TypeInsnNode n = (TypeInsnNode) insn;
				printf("%s", n.desc);
			}
			else if(insn instanceof IincInsnNode)
			{
				IincInsnNode n = (IincInsnNode) insn;
				printf("%s %s", n.var, n.incr);
			}

			printf(" (%s)\n", insn.getClass().getSimpleName());
		}
	}
	
	public static void printf(String format, Object... params)
	{
		FMLCommonHandler.instance().getFMLLogger().printf(Level.DEBUG, format, params);
		System.out.printf(format, params);
	}
}
