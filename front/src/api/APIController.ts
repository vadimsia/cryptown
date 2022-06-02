import type { APIGetRegionResponse } from './ResponseModels/APIGetRegionResponse';

export class APIController {
	private static async get(path: string): Promise<any> {
		const response = await fetch(path);
		return await response.json();
	}

	public static async getRegion(id: number): Promise<APIGetRegionResponse> {
		const response = (await this.get(`/api/get_region/${id}`)) as APIGetRegionResponse;
		return response;
	}
}
