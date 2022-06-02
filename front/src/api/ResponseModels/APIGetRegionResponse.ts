import type { APIResponse } from '../APIResponse';

export interface APIGetRegionResponse extends APIResponse {
	data: {
		region_raw: string;
	};
}
