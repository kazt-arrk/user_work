LICENSE = "CLOSED"
DESCRIPTION = "launcher taskbar"

SRC_URI = " \
 file://launcher.init \
"

S = "${WORKDIR}/launcher-taskbar"

SRC = "${THISDIR}/launcher-taskbar"

inherit update-rc.d
INITSCRIPT_NAME = "launcher.init"
INITSCRIPT_PARAMS = "start 99 2 3 4 5 . stop 99 0 1 6 ."

do_install() {
    install -d ${D}/home/root/launcher-taskbar
    install -m 755 ${SRC}/icons/* ${D}/home/root/launcher-taskbar/

    install -d ${D}/etc/init.d
    install -m 755 ${SRC}/launcher.init ${D}/etc/init.d/launcher.init
}

do_install_append() {
    install -m 755 ${SRC}/aprzg0a_scripts/* ${D}/home/root/launcher-taskbar/
}

FILES_${PN} = "/home/root/launcher-taskbar/* \
	/etc/init.d \
	/home/root/start_files/* \
	/home/root \
"
FILES_${PN}-dbg += " \
 /home/root/launcher-taskbar/.debug/* \
"
