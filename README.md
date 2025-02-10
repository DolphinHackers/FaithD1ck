# FaithD1ck
* Faiths Client but fxxking opensource, used for Bl0cksMC
## How 2 run? (IntelliJ IDEA)
1. Add a new application launch configuration
2. Set the main class to `net.minecraft.client.main.Main`
3. Add the following VM arguments: `-Dorg.lwjgl.librarypath=$MODULE_DIR$/target/dependency`
4. Add the following program arguments: `--version mcp --accessToken 0 --assetsDir assets --assetIndex 1.8 --userProperties {}`
5. Add a before launch maven goal: `dependency:unpack-dependencies -Dmdep.unpack.includes=**/*.dll,**/*.so,**/*.jnilib,**/*.dylib`
6. Run the configuration