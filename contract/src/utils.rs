pub struct Conversations;

impl Conversations {
    pub fn as_u32_be(array: &[u8]) -> u32 {
        ((array[0] as u32) << 24) +
            ((array[1] as u32) << 16) +
            ((array[2] as u32) <<  8) +
            ((array[3] as u32) <<  0)
    }

    pub fn as_u32_le(array: &[u8]) -> u32 {
        ((array[3] as u32) << 24) +
            ((array[2] as u32) << 16) +
            ((array[1] as u32) <<  8) +
            ((array[0] as u32) <<  0)
    }

    pub fn as_u8_le(x: u32) -> [u8;4] {
        let b4 : u8 = ((x >> 24) & 0xff) as u8;
        let b3 : u8 = ((x >> 16) & 0xff) as u8;
        let b2 : u8 = ((x >> 8) & 0xff) as u8;
        let b1 : u8 = (x & 0xff) as u8;
        return [b1, b2, b3, b4]
    }

    pub fn as_u8_be(x: u32) -> [u8;4] {
        let b1 : u8 = ((x >> 24) & 0xff) as u8;
        let b2 : u8 = ((x >> 16) & 0xff) as u8;
        let b3 : u8 = ((x >> 8) & 0xff) as u8;
        let b4 : u8 = (x & 0xff) as u8;
        return [b1, b2, b3, b4]
    }
}