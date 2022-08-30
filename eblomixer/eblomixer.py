from PIL import Image, ImageDraw, ImageFont
import json

from hashlib import md5

class Asset:
  image = None

  id = None
  name = None
  dir = None
  
  def __init__(self, dir, name):
    self.image = Image.open(f'{dir}/{name}').convert('RGBA')

    self.dir = dir
    self.id = name.split(' ')[0]
    self.name = ' '.join(name.split(' ')[1:]).split('.')[0]

class Art:
  background = None
  metadata = None

  h = None

  def __init__(self, width: int, height: int, index: int) -> None:
    self.background = Image.new('RGBA', (width, height))

    self.h = md5(str(index).encode()).hexdigest()

    self.metadata = {
      "name": f"Cryptown Token #{index}",
      "symbol": "CRYPTOWN",
      "description": "Minecraft Cryptown region owner token",
      "seller_fee_basis_points": 100,
      "image": f"{index}.png",
      "external_url": "https://cryptown.com",
      "attributes": [],
      "collection": { "name": "CRYPTOWN", "family": "CRYPTOWN" },
      "properties": {
        "files": [
          {
            "uri": f"{index}.png",
            "type": "image/png"
          }
        ],
        "creators": [
          {
            "address": "ECCjGknHtdDMkjn8fJ2jPKT143AxsHWzHAd2Uw3iqj4g",
            "share": 100
          }
        ]
      }
    }

  def putAsset(self, trait_type: str, asset: Asset) -> None:
    self.background.paste(asset.image, (0, 0), asset.image)
  
  def putIndex(self, index: int) -> None:
    draw = ImageDraw.ImageDraw(self.background)

    font = ImageFont.truetype("Roboto-Bold.ttf", 28)

    draw.text((10, 10), f"#{index}", font=font)

  def save(self, dir: str, name: str):
    self.background.save(f'{dir}/{name}.png')
    
    with open(f'{dir}/{name}.json', 'w') as f:
      json.dump(self.metadata, f, indent=2)


for i in range(0, 10):
  art = Art(512, 512, i)
  asset = Asset('images', 'background.png')

  art.putAsset('', asset)
  art.putIndex(i)

  art.save('mixed', str(i))