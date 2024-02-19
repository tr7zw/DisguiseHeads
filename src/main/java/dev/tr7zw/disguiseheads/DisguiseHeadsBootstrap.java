//#if FORGE
//$$package dev.tr7zw.disguiseheads;
//$$
//$$import net.minecraftforge.api.distmarker.Dist;
//$$import net.minecraftforge.fml.DistExecutor;
//$$import net.minecraftforge.fml.common.Mod;
//$$
//$$@Mod("disguiseheads")
//$$public class DisguiseHeadsBootstrap {
//$$
//$$	public DisguiseHeadsBootstrap() {
//$$		DistExecutor.unsafeRunWhenOn(Dist.CLIENT, () -> () -> { 
//$$         new DisguiseHeadsShared().init();
//$$        });
//$$	}
//$$	
//$$}
//#elseif NEOFORGE
//$$package dev.tr7zw.disguiseheads;
//$$
//$$import net.neoforged.api.distmarker.Dist;
//$$import net.neoforged.fml.DistExecutor;
//$$import net.neoforged.fml.common.Mod;
//$$
//$$@Mod("disguiseheads")
//$$public class DisguiseHeadsBootstrap {
//$$
//$$	public DisguiseHeadsBootstrap() {
//$$		DistExecutor.unsafeRunWhenOn(Dist.CLIENT, () -> () -> { 
//$$         new DisguiseHeadsShared().init();
//$$        });
//$$	}
//$$	
//$$}
//#endif
