package com.crypteam.rpc;

import com.crypteam.Section;
import com.crypteam.rpc.requests.AuthorizeRequest;
import com.crypteam.rpc.requests.ReadDataRequest;
import com.crypteam.rpc.requests.ReadDataResponse;
import com.crypteam.solana.misc.PublicKey;
import com.crypteam.solana.misc.RegionAccountInfo;
import com.crypteam.solana.program.CryptownProgram;
import com.sk89q.worldedit.bukkit.BukkitAdapter;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.entity.Player;
import redis.clients.jedis.JedisPubSub;

import java.io.IOException;
import java.util.List;
import java.util.UUID;

public class RPCSubscriber extends JedisPubSub {
    public void onMessage(String channel, String message) {
        RPCRequest request;
        try {
            request = (RPCRequest) Serializer.deserialize(message);
        } catch (Exception e) {
            System.out.println("ERROR");
            e.printStackTrace();
            return;
        }

        System.out.println("Got COMMAND " + request.command);

        if (request.command == RPCCommand.READ_DATA) {
            ReadDataRequest typed_request = (ReadDataRequest) request;
            Section sec = new Section(typed_request.area_id);

            ReadDataResponse response = new ReadDataResponse(typed_request, sec.getRegion());

            try {
                RPCPublisher.publish(Serializer.serialize(response));
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
        else if (request.command == RPCCommand.AUTHORIZE_USER) {
            CryptownProgram program = new CryptownProgram();
            AuthorizeRequest typed_request = (AuthorizeRequest) request;
            List<RegionAccountInfo> regions;

            System.out.println("PK: " + new PublicKey(typed_request.key));

            try {
                 regions = program.getRegionsByOwner(new PublicKey(typed_request.key));
            } catch (Exception e) {
                e.printStackTrace();
                return;
            }

            System.out.println("Regions: " + regions.size());

            for (RegionAccountInfo region : regions) {
                Player player = Bukkit.getServer().getPlayer(UUID.fromString(typed_request.uuid));
                assert player != null;
                player.sendMessage(ChatColor.GREEN + "You got region with id: " + region.getId());
                new Section(region.getId()).setRegionAccess(BukkitAdapter.adapt(player));
            }
        }
    }

    public void onSubscribe(String channel, int subscribedChannels) {
        System.out.println("RPC Listener started!");
    }

    public void onUnsubscribe(String channel, int subscribedChannels) {
        System.out.println("RPC Listener stopped!");
    }
}