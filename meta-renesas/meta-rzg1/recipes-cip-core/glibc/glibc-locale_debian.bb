require glibc-collateral.inc

SUMMARY = "Locale data from glibc"

BPN = "glibc"

# very rare case; glibc-locale doen't have source tree but
# generates binary packages. DEBIAN_UNPACK_DIR should point
# the glibc source tree instead so that summary.bbclass can find changelog.
GLIBC_PN = "${@d.getVar('PN', True).replace('-locale', '')}"
do_debian_verify_version[depends] += "${GLIBC_PN}:do_unpack"
DEBIAN_UNPACK_DIR = "${@d.getVar('WORKDIR', True).replace(d.getVar('PN', True), d.getVar('GLIBC_PN', True))}/git"


LOCALEBASEPN = "${MLPREFIX}glibc"

# glibc-collateral.inc inhibits all default deps, but do_package needs objcopy
# ERROR: objcopy failed with exit code 127 (cmd was 'i586-webos-linux-objcopy' --only-keep-debug 'glibc-locale/2.17-r0/package/usr/lib/gconv/IBM1166.so' 'glibc-locale/2.17-r0/package/usr/lib/gconv/.debug/IBM1166.so')
# ERROR: Function failed: split_and_strip_files
BINUTILSDEP = "virtual/${MLPREFIX}${TARGET_PREFIX}binutils:do_populate_sysroot"
BINUTILSDEP_class-nativesdk = "virtual/${TARGET_PREFIX}binutils-crosssdk:do_populate_sysroot"
do_package[depends] += "${BINUTILSDEP}"

# Binary locales are generated at build time if ENABLE_BINARY_LOCALE_GENERATION
# is set. The idea is to avoid running localedef on the target (at first boot)
# to decrease initial boot time and avoid localedef being killed by the OOM
# killer which used to effectively break i18n on machines with < 128MB RAM.

# default to disabled 
ENABLE_BINARY_LOCALE_GENERATION ?= "0"
ENABLE_BINARY_LOCALE_GENERATION_pn-nativesdk-glibc-locale = "0"

#enable locale generation on these arches
# BINARY_LOCALE_ARCHES is a space separated list of regular expressions
BINARY_LOCALE_ARCHES ?= "arm.* aarch64 i[3-6]86 x86_64 powerpc mips mips64"

# set "1" to use cross-localedef for locale generation
# set "0" for qemu emulation of native localedef for locale generation
LOCALE_GENERATION_WITH_CROSS-LOCALEDEF = "0"

PROVIDES = "virtual/libc-locale"

PACKAGES = "localedef ${PN}-dbg"

PACKAGES_DYNAMIC = "^locale-base-.* \
                    ^glibc-locale-.* \
                    ^glibc-gconv-.* ^glibc-charmap-.* ^glibc-localedata-.* ^glibc-binary-localedata-.* \
                    ^${MLPREFIX}glibc-gconv$"

# Create a glibc-binaries package
ALLOW_EMPTY_${BPN}-binaries = "1"
PACKAGES += "${BPN}-binaries"
RRECOMMENDS_${BPN}-binaries =  "${@" ".join([p for p in d.getVar('PACKAGES', True).split() if p.find("glibc-binary") != -1])}"

# Create a glibc-charmaps package
ALLOW_EMPTY_${BPN}-charmaps = "1"
PACKAGES += "${BPN}-charmaps"
RRECOMMENDS_${BPN}-charmaps =  "${@" ".join([p for p in d.getVar('PACKAGES', True).split() if p.find("glibc-charmap") != -1])}"

# Create a glibc-gconvs package
ALLOW_EMPTY_${BPN}-gconvs = "1"
PACKAGES += "${BPN}-gconvs"
RRECOMMENDS_${BPN}-gconvs =  "${@" ".join([p for p in d.getVar('PACKAGES', True).split() if p.find("glibc-gconv") != -1])}"

# Create a glibc-localedatas package
ALLOW_EMPTY_${BPN}-localedatas = "1"
PACKAGES += "${BPN}-localedatas"
RRECOMMENDS_${BPN}-localedatas =  "${@" ".join([p for p in d.getVar('PACKAGES', True).split() if p.find("glibc-localedata") != -1])}"

# Create a locales-all package
ALLOW_EMPTY_locales-all = "1"
PACKAGES += "locales-all"
RRECOMMENDS_locales-all =  "${@" ".join([p for p in d.getVar('PACKAGES', True).split() if p.find("locale-base-") != -1])}"

DESCRIPTION_localedef = "glibc: compile locale definition files"

# glibc-gconv is dynamically added into PACKAGES, thus
# FILES_glibc-gconv will not be automatically extended in multilib.
# Explicitly add ${MLPREFIX} for FILES_glibc-gconv.
FILES_${MLPREFIX}glibc-gconv = "${libdir}/gconv/*"
FILES_localedef = "${bindir}/localedef"

LOCALETREESRC = "${STAGING_INCDIR}/glibc-locale-internal-${MULTIMACH_TARGET_SYS}"

do_install () {
	mkdir -p ${D}${bindir} ${D}${datadir}
	if [ -n "$(ls ${LOCALETREESRC}${bindir})" ]; then
		cp -fpPR ${LOCALETREESRC}${bindir}/* ${D}${bindir}
	fi
	if [ -n "$(ls ${LOCALETREESRC}${localedir})" ]; then
		mkdir -p ${D}${localedir}
		cp -fpPR ${LOCALETREESRC}${localedir}/* ${D}${localedir}
	fi

	# Only install /usr/lib/gconv and /usr/share/i18n if charset/locales/locale-code is enabled
	# if not, there will be QA Issue "Files/directories were installed but not shipped"
	if [ -e ${LOCALETREESRC}${libdir}/gconv ] && [ ${PACKAGE_NO_GCONV} = 0 ]; then
		mkdir -p ${D}${libdir}
		cp -fpPR ${LOCALETREESRC}${libdir}/gconv ${D}${libdir}
	fi
	if [ -e ${LOCALETREESRC}${datadir}/i18n ] && [ ${PACKAGE_NO_GCONV} = 0 ]; then
		cp -fpPR ${LOCALETREESRC}${datadir}/i18n ${D}${datadir}
	fi

	if [ -e ${LOCALETREESRC}${datadir}/locale ]; then
		cp -fpPR ${LOCALETREESRC}${datadir}/locale ${D}${datadir}
	fi
	chown root.root -R ${D}
	cp -fpPR ${LOCALETREESRC}/SUPPORTED ${WORKDIR}

	# According to debhelper.in/locales.install
	install -d ${D}${sysconfdir} ${D}${sbindir}
	install -m 0644 ${D}${datadir}/locale/locale.alias ${D}${sysconfdir}/
	install -m 0755 ${S}/debian/local/usr_sbin/locale-gen ${D}${sbindir}/
	install -m 0755 ${S}/debian/local/usr_sbin/update-locale ${D}${sbindir}/
	install -m 0755 ${S}/debian/local/usr_sbin/validlocale ${D}${sbindir}/
}

inherit libc-package

BBCLASSEXTEND = "nativesdk"

PACKAGES += "locales"
RRECOMMENDS_locales += " \
    ${@" ".join([p for p in d.getVar('PACKAGES', True).split() if p.find("glibc-locale-") != -1])} \
    ${@" ".join([p for p in d.getVar('PACKAGES', True).split() if p.find("glibc-localedata-") != -1])} \
    ${@" ".join([p for p in d.getVar('PACKAGES', True).split() if p.find("glibc-charmap-") != -1])} \
"
FILES_locales = " \
    ${sysconfdir}/locale.alias \
    ${sbindir}/* \
    ${datadir}/locale/locale.alias \
"
RDEPENDS_locales = "perl"

FILES_${PN}-dbg += "${libdir}/gconv/.debug"

# There are many many warning message related to locale-base-* packages
# since they redepends on glibc-binary-localedata-* but not depend.
# These warning messages are harmless, suppress them here.
do_package_qa[noexec] = "1"
