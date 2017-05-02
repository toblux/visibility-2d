debug:
	lein cljsbuild once debug

release:
	lein cljsbuild once release

figwheel:
	lein figwheel

clean:
	lein clean

demo: release
	cp resources/public/index.html docs/index.html
	cp resources/public/js/visibility-2d.min.js docs/js/visibility-2d.min.js
	cp resources/public/css/main.css docs/css/main.css

.PHONY: debug release figwheel clean demo
