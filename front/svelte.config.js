import adapter from '@sveltejs/adapter-static';
import preprocess from 'svelte-preprocess';

/** @type {import('@sveltejs/kit').Config} */
const config = {
	// Consult https://github.com/sveltejs/svelte-preprocess
	// for more information about preprocessors
	preprocess: preprocess(),

	kit: {
		adapter: adapter({
			// default options are shown
			pages: 'build',
			assets: 'build/static',
			fallback: 'index.html'
		}),
		vite: {
			server: {
				proxy: {
					'/api': 'http://localhost:8080'
				}
			}
		}
	}
};

export default config;
