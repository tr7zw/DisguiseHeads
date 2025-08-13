//#if FORGE
//$$package dev.tr7zw.disguiseheads;
//$$
//$$import net.minecraftforge.api.distmarker.Dist;
//$$import net.minecraftforge.fml.DistExecutor;
//$$import net.minecraftforge.fml.common.Mod;
//$$import net.minecraftforge.fml.javafmlmod.FMLJavaModLoadingContext;
//$$import dev.tr7zw.transition.loader.ModLoaderUtil;
//$$
//$$@Mod("disguiseheads")
//$$public class DisguiseHeadsBootstrap {
//$$
//$$	public DisguiseHeadsBootstrap(FMLJavaModLoadingContext context) {
//$$        ModLoaderUtil.setModLoadingContext(context);
//$$		DistExecutor.unsafeRunWhenOn(Dist.CLIENT, () -> () -> { 
//$$         new DisguiseHeadsShared().init();
//$$        });
//$$	}
//$$    public DisguiseHeadsBootstrap() {
//$$        this(FMLJavaModLoadingContext.get());
//$$    }
//$$	
//$$}
//#elseif NEOFORGE
//$$package dev.tr7zw.disguiseheads;
//$$
//$$import net.neoforged.fml.loading.FMLEnvironment;
//$$import net.neoforged.fml.common.Mod;
//$$import dev.tr7zw.transition.loader.ModLoaderEventUtil;
//$$
//$$@Mod("disguiseheads")
//$$public class DisguiseHeadsBootstrap {
//$$
//$$    public DisguiseHeadsBootstrap() {
//$$            if (FMLEnvironment.dist.isClient()){
//$$                    ModLoaderEventUtil.registerClientSetupListener(() -> new DisguiseHeadsShared().init());
//$$            }
//$$    }
//$$
//$$}
//#endif