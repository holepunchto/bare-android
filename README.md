# Bare on Android

Example of embedding Bare in an Android application using <https://github.com/holepunchto/bare-kit>.

## Building

To keep the build process fast and efficient, the project relies on a Bare Kit prebuild being available in the [`app/libs/`](app/libs) directory. Prior to building the project, you must therefore either clone and compile Bare Kit from source, or download the latest prebuild from GitHub. The latter is easily accomplished using the [GitHub CLI](https://cli.github.com):

```console
gh release download --repo holepunchto/bare-kit <version>
```

Unpack the resulting `prebuilds.zip` archive and move `android/bare-kit` into [`app/libs/`](app/libs). When finished, either open the project in Android Studio or build it from the commandline:

```console
gradle build
```

### Addons

Native addons will be linked into [`app/src/main/addons/`](app/src/main/addons) as part of the build process and will be automatically included in the final APK bundle by Gradle.

## License

Apache-2.0
