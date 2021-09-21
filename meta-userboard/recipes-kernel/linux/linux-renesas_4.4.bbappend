FILESEXTRAPATHS_prepend := "${THISDIR}/${PN}:"

do_configure_append() {
    # Start custom kernel configuration

    # Finish custom kernel configuration

    yes '' | oe_runmake oldconfig
}

