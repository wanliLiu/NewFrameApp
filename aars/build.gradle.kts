
configurations.maybeCreate("libcommon")
artifacts.add("libcommon", file("libs/fragmentation-1.0.3.aar"))
artifacts.add("libcommon", file("libs/fragmentation_core-1.0.3.aar"))
artifacts.add("libcommon", file("libs/fragmentation_swipeback-1.0.3.aar"))
artifacts.add("libcommon", file("libs/pullupdownrefresh-1.0.0.aar"))

configurations.maybeCreate("flycotablayout")
artifacts.add("flycotablayout", file("libs/flycotablayout-2.1.5.aar"))