<script lang="ts">

</script>

<div class="server">
    <div class="name">Server</div>
    
    <div class="boop"></div>
    <div class="params">
        <div class="online">
            <div class="param">Online:</div>
            <div class="value">0/2000</div>
        </div>
        <div class="ip">
            <div class="param">Server IP:</div>
            <div class="value">minecraft.cryptown.one</div>
        </div>
    </div>
    
</div>

<style>
    .server {
        display: flex;
        flex-direction: column;
        justify-content: center;
        align-items: center;
        width: 400px;
        height: 200px;
        margin-top: 10px;
        background-color: #00000096;
		border-radius: 4px;
        padding: 10px;
    }
    
    .name {
        padding: 5px;
        border-radius: 7px;
        font-size: 2rem;
        color: rgb(1, 173, 3);
        border: 2px solid;
        margin-bottom: 10px;
    }

    .ip {
        font-size: 1rem;
        display: flex;
        flex-direction: row;
    }

    .online {
        justify-content: space-between;
        display: flex;
        flex-direction: row;
        font-size: 1rem;
        margin-bottom: 10px;
    }

    .param {
        border-radius: 4px;
        padding: 3px;
        color: #e9e9e9e8;
        margin-right: 5px;
        background: rgb(1, 173, 3);
    }

    .value {
        display: flex;
        justify-content: center;
        width: 180px;
        margin-top: 3px;
        color: #e9e9e9e8;
    }

    .boop {
        background-color: rgb(1, 173, 3);
        width: 20px;
        height: 20px;
        border-radius: 50%;
        margin: 10px;
    }


    
</style>