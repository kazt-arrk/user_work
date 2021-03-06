SUMMARY = "gbm library"
LICENSE = "MIT"
SECTION = "libs"

FILESEXTRAPATHS_prepend := "${THISDIR}/${PN}:"

LIC_FILES_CHKSUM = " \
    file://gbm.c;beginline=4;endline=22;md5=5cdaac262c876e98e47771f11c7036b5"

SRCREV = "a0c7d6c97fe1fffe45eee524060cbb12767c6461"
SRC_URI = "git://github.com/renesas-rcar/libgbm;branch=rcar-gen3 \
			file://0001-Revert-backend_kms-do-not-allocate-KMS-BO-when-gbm-s.patch \
"

S = "${WORKDIR}/git"

COMPATIBLE_MACHINE = "(r8a7742|r8a7743|r8a7745|r8a77470)"
DEPENDS = "wayland-kms udev"

inherit autotools pkgconfig

PACKAGES = " \
    ${PN} \
    ${PN}-dev \
    ${PN}-dbg \
    ${PN}-staticdev \
"

FILES_${PN} = " \
    ${libdir}/libgbm.so.* \
    ${libdir}/gbm/libgbm_kms.so.* \
    ${libdir}/gbm/*.so \
    ${libdir}/*.so \
"
FILES_${PN}-dev += "${libdir}/gbm/*.la"
FILES_${PN}-dbg += "${libdir}/gbm/.debug/*"
FILES_${PN}-staticdev += "${libdir}/gbm/*.a"

INSANE_SKIP_${PN} += "dev-so"
