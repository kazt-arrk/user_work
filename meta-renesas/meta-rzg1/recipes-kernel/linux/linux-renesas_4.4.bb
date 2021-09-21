DESCRIPTION = "Linux kernel for the RZ/G based board"

require recipes-kernel/linux/linux-yocto.inc
include linux.inc

FILESEXTRAPATHS_prepend := "${THISDIR}/${PN}/:"
COMPATIBLE_MACHINE = "iwg20m|iwg21m|iwg22m|iwg23s|aprzg0a"

DEPENDS_append = " util-linux-native openssl-native"

RENESAS_URL="git://github.com/renesas-rz/renesas-cip.git"
SRCREV = "314face074138ffb645e71b87e9d72aab2311233"
SRC_URI = " \
	${RENESAS_URL};protocol=git;branch=v4.4.138-cip25 \
	file://0001-v4l2-core-remove-unhelpful-kernel-warning.patch \
	file://0001-include-uapi-linux-if_pppox.h-include-linux-in.h-and.patch \
"

LINUX_VERSION ?= "4.4.138-cip25"
PV = "${LINUX_VERSION}+git${SRCPV}"
PR = "r1"

S = "${WORKDIR}/git"

SRC_URI_append = " \
    file://defconfig \
    file://common.cfg \
"

SRC_URI_append_iwg20m = " \
    file://iwg20m.cfg \
"

SRC_URI_append_iwg21m = " \
    file://iwg21m.cfg \
"

SRC_URI_append_iwg22m = " \
    file://iwg22m.cfg \
"

SRC_URI_append_iwg23s = " \
    file://iwg23s.cfg \
"

SRC_URI_append_aprzg0a = " \
    file://aprzg0a.cfg \
    file://0010-aprzg0a_linux-v2.00.patch \
    file://0011-aprzg0a_linux-v2.01.patch \
    file://0012-aprzg0a_linux-v2.02.patch \
"

do_configure_append() {
	# If kernel_configure_variable or similar functions is used, add here.
	# Note that the settings here has higher priority than cfg files above.

}
